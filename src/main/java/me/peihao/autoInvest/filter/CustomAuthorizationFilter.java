package me.peihao.autoInvest.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.peihao.autoInvest.constant.ResultInfoConstants;
import me.peihao.autoInvest.service.TokenService;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import static me.peihao.autoInvest.common.ResultUtil.buildJson;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
  private final TokenService tokenService;

  public CustomAuthorizationFilter(TokenService tokenService){
    this.tokenService = tokenService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    if (request.getServletPath().equals("/api/v1/login") ||
        request.getServletPath().equals("/api/v1/refresh/token") ||
        request.getServletPath().equals("/api/v1/registration")) {
      filterChain.doFilter(request, response);
    } else {
      String authorizationHeader = request.getHeader(AUTHORIZATION);
      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
        try {
          String accessToken = authorizationHeader.substring("Bearer".length()).trim();
          SecurityContextHolder.getContext().setAuthentication(tokenService.authenticationToken(accessToken));
          filterChain.doFilter(request, response);
        } catch (TokenExpiredException exception) {
          response.setStatus(UNAUTHORIZED.value());
          response.setContentType(String.valueOf(MediaType.APPLICATION_JSON));
          new ObjectMapper().writeValue(response.getOutputStream(),
              buildJson(ResultInfoConstants.TOKEN_EXPIRED, "token is expired"));
        } catch (SignatureVerificationException | JWTDecodeException exception){
          response.setStatus(UNAUTHORIZED.value());
          response.setContentType(String.valueOf(MediaType.APPLICATION_JSON));
          new ObjectMapper().writeValue(response.getOutputStream(),
              buildJson(ResultInfoConstants.INVALID_TOKEN, "invalid token"));
        } catch (Exception exception){
          log.error("Unexpected error: cause: {} - {}", exception.getCause(), exception.getMessage());
          response.setStatus(INTERNAL_SERVER_ERROR.value());
          response.setContentType(String.valueOf(MediaType.APPLICATION_JSON));
          new ObjectMapper().writeValue(response.getOutputStream(),
              buildJson(ResultInfoConstants.SYSTEM_ERROR, "system error"));
        }
      } else {
        response.setStatus(UNAUTHORIZED.value());
        response.setContentType(String.valueOf(MediaType.APPLICATION_JSON));
        new ObjectMapper().writeValue(response.getOutputStream(),
            buildJson(ResultInfoConstants.UNAUTHORIZED, "unauthorized"));
      }
    }
  }
}
