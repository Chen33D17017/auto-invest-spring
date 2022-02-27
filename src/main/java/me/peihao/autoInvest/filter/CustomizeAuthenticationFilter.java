package me.peihao.autoInvest.filter;

import static me.peihao.autoInvest.common.ResultUtil.buildJson;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.FilterChain;
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
import me.peihao.autoInvest.service.TokenService;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

@Slf4j
@AllArgsConstructor
public class CustomizeAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final TokenService tokenService;

  @SneakyThrows
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) {
    RequestWrapper wrapper = new RequestWrapper(request);
    byte[] body = StreamUtils.copyToByteArray(wrapper.getInputStream());
    LoginRequestDTO loginRequestDTO = new ObjectMapper().readValue(body, LoginRequestDTO.class);
    request.setAttribute("remember_me", loginRequestDTO.getRememberMe());

    log.info("User {} is trying to login", loginRequestDTO.getUsername());

    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        loginRequestDTO.getUsername(), loginRequestDTO.getPassword());

    return authenticationManager.authenticate(authenticationToken);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException {
    Boolean rememberMe = (Boolean) request.getAttribute("remember_me");
    AppUser user = (AppUser) authResult.getPrincipal();
    TokenResponseDTO tokenResponseDTO = tokenService.generateJWTResponse(user, rememberMe);

    response.setContentType(String.valueOf(MediaType.APPLICATION_JSON));
    new ObjectMapper().writeValue(response.getOutputStream(), buildJson(ResultInfoConstants.SUCCESS,
        tokenResponseDTO));
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed)
      throws IOException {
    response.setStatus(FORBIDDEN.value());
    response.setContentType(String.valueOf(MediaType.APPLICATION_JSON));
    new ObjectMapper().writeValue(response.getOutputStream(),
        buildJson(ResultInfoConstants.BAD_CREDENTIAL, "BAD CREDENTIAL"));
  }
}
