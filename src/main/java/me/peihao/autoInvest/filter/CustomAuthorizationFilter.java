package me.peihao.autoInvest.filter;

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
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
  private final TokenService tokenService;

  public CustomAuthorizationFilter(TokenService tokenService){
    this.tokenService = tokenService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    if (request.getServletPath().equals("/api/v1/login") || request.getServletPath().equals("/api/v1/refresh/token")) {
      filterChain.doFilter(request, response);
    } else {
      String authorizationHeader = request.getHeader(AUTHORIZATION);
      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
        try {
          String accessToken = authorizationHeader.substring("Bearer".length()).trim();
          SecurityContextHolder.getContext().setAuthentication(tokenService.authenticationToken(accessToken));
          filterChain.doFilter(request, response);
        } catch (Exception exception) {
          log.error("Error logging in : {}", exception.getMessage());
          response.setStatus(FORBIDDEN.value());
          response.setContentType(String.valueOf(MediaType.APPLICATION_JSON));
          new ObjectMapper().writeValue(response.getOutputStream(),
              buildJson(ResultInfoConstants.INVALID_TOKEN, exception.getMessage()));
        }
      } else {
        filterChain.doFilter(request, response);
      }
    }
  }
}
