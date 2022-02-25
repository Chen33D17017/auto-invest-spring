package me.peihao.autoInvest.filter;

import static me.peihao.autoInvest.common.ResultUtil.buildJson;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.peihao.autoInvest.common.RequestWrapper;
import me.peihao.autoInvest.constant.ResultInfoConstants;
import me.peihao.autoInvest.dto.requests.LoginRequestDTO;
import me.peihao.autoInvest.dto.response.TokenResponseDTO;
import me.peihao.autoInvest.model.AppUser;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

@Slf4j
@AllArgsConstructor
public class CustomizeAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final String signSecret;

  @SneakyThrows
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) {
    RequestWrapper wrapper = new RequestWrapper(request);

    byte[] body = StreamUtils.copyToByteArray(wrapper.getInputStream());

    LoginRequestDTO loginRequestDTO = new ObjectMapper().readValue(body, LoginRequestDTO.class);

    log.info("User {} is trying to login", loginRequestDTO.getUsername());

    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        loginRequestDTO.getUsername(), loginRequestDTO.getPassword());

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

    TokenResponseDTO tokenResponseDTO = TokenResponseDTO.builder().accessToken(access_token).refreshToken(refresh_token)
        .build();

    response.setContentType(String.valueOf(MediaType.APPLICATION_JSON));
    new ObjectMapper().writeValue(response.getOutputStream(), buildJson(ResultInfoConstants.SUCCESS,
        tokenResponseDTO));
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed)
      throws IOException, ServletException {
    response.setStatus(FORBIDDEN.value());
    response.setContentType(String.valueOf(MediaType.APPLICATION_JSON));
    new ObjectMapper().writeValue(response.getOutputStream(),
        buildJson(ResultInfoConstants.BAD_CREDENTIAL, "BAD CREDENTIAL"));
  }
}
