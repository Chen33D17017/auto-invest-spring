package me.peihao.autoInvest.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Arrays;
import java.util.Collections;
import me.peihao.autoInvest.filter.CustomAuthorizationFilter;
import me.peihao.autoInvest.filter.CustomizeAuthenticationFilter;
import me.peihao.autoInvest.service.AppUserService;
import me.peihao.autoInvest.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final AppUserService appUserService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final TokenService tokenService;

  public WebSecurityConfig(AppUserService appUserService,
      BCryptPasswordEncoder bCryptPasswordEncoder, TokenService tokenService) {
    this.appUserService = appUserService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.tokenService = tokenService;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    CustomizeAuthenticationFilter customizeAuthenticationFilter = new CustomizeAuthenticationFilter(authenticationManagerBean(), tokenService);
    customizeAuthenticationFilter.setFilterProcessesUrl("/api/v1/login");
    http.csrf().disable().cors(withDefaults());
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.authorizeRequests()
        .antMatchers("/api/v1/registration/**", "/api/v1/login", "/api/v1/refresh/token/**").permitAll()
        .antMatchers("/**").hasAnyAuthority("USER", "ADMIN");
    http.addFilter(customizeAuthenticationFilter);
    http.addFilterBefore(new CustomAuthorizationFilter(tokenService), UsernamePasswordAuthenticationFilter.class);
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(daoAuthenticationProvider());
  }

  // Ref: https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/dao-authentication-provider.html
  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider(){
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(bCryptPasswordEncoder);
    provider.setUserDetailsService(appUserService);
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }
}
