package me.peihao.autoInvest.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.peihao.autoInvest.model.AppUser;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@AllArgsConstructor
public class CustomizeAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final AuthenticationManager authenticationManager;
  private final String signSecret;

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    String basicHeader = request.getHeader("Authorization").substring("Basic".length()).trim();
    Base64.Decoder dec = Base64.getDecoder();
    String[] credential = new String(dec.decode(basicHeader)).split(":");
    String username = credential[0];
    String password = credential[1];
    log.info("User {} is trying to login", username);
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
    return authenticationManager.authenticate(authenticationToken);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {
    AppUser user = (AppUser) authResult.getPrincipal();
    Algorithm algorithm = Algorithm.HMAC256(signSecret.getBytes());
    String access_token = JWT.create()
        .withSubject(user.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10)))
        .withIssuer("RegularInvestDAO")
        .withClaim("roles",
            user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(
                Collectors.toList())).sign(algorithm);

    String refresh_token = JWT.create()
        .withSubject(user.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)))
        .withIssuer("RegularInvestDAO").sign(algorithm);

    Map<String, String> tokens = new HashMap<>();
    tokens.put("access_token", access_token);
    tokens.put("refresh_token", refresh_token);
    response.setContentType(String.valueOf(MediaType.APPLICATION_JSON));
    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
  }
}
