package me.peihao.autoInvest.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.security.Principal;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import static me.peihao.autoInvest.common.ResultUtil.generateSuccessResponse;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import me.peihao.autoInvest.constant.ResultInfoConstants;
import me.peihao.autoInvest.dto.requests.PatchUserRequestDTO;
import me.peihao.autoInvest.dto.response.TokenDTO;
import me.peihao.autoInvest.exception.AutoInvestException;
import me.peihao.autoInvest.service.AppUserService;
import me.peihao.autoInvest.dto.requests.RegistrationUserRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class AppUserController {

  private final AppUserService appUserService;
  private final String signSecret;

  @Autowired
  public AppUserController(AppUserService appUserService, @Value("jwt.sign-secret") String signSecret){
    this.appUserService = appUserService;
    this.signSecret = signSecret;
  }

  @PostMapping("/v1/registration")
  public ResponseEntity<String> register(
      @RequestBody RegistrationUserRequestDTO registrationUserRequestDTO) {
    return generateSuccessResponse(appUserService.register(registrationUserRequestDTO));
  }

  @GetMapping("/v1/registration/confirm")
  public ResponseEntity<String> confirmRegistration(@PathParam("token") String token){
    return generateSuccessResponse(appUserService.confirmToken(token));
  }

  @PatchMapping("/v1/user")
  public ResponseEntity<String> updateUserInfo(Principal principal, @RequestBody PatchUserRequestDTO patchUserRequestDTO){
    return generateSuccessResponse(appUserService.patchUser(patchUserRequestDTO, principal.getName()));
  }

  @GetMapping("/v1/user")
  public  ResponseEntity<String> getUserInfo(Principal principal){
    return generateSuccessResponse(appUserService.getUserInfo(principal.getName()));
  }

  @PostMapping("/reissue")
  public ResponseEntity<String> reissueConfirmationToken(
      @PathParam("username") String username){
    return generateSuccessResponse(
        appUserService.reissueConfirmationToken(username));
  }

  @GetMapping("/refresh/token")
  public ResponseEntity<String> refreshToken(HttpServletRequest request,
      HttpServletResponse response) {
    String accessToken;
    String refreshToken;
    String authorizationHeader = request.getHeader(AUTHORIZATION);
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
      refreshToken = authorizationHeader.substring("Bearer".length()).trim();
      Algorithm algorithm = Algorithm.HMAC256(signSecret.getBytes());
      JWTVerifier verifier = JWT.require(algorithm).build();
      DecodedJWT decodedJWT = verifier.verify(refreshToken);
      String username = decodedJWT.getSubject();
      UserDetails user = appUserService.loadUserByUsername(username);
      accessToken = JWT.create()
          .withSubject(user.getUsername())
          .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMicros(10)))
          .withIssuer("RegularInvestDAO")
          .withClaim("roles",
              user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(
                  Collectors.toList())).sign(algorithm);;
    } else {
      throw new AutoInvestException(ResultInfoConstants.MISSING_REFRESH_TOKEN);
    }
    return generateSuccessResponse(
        new TokenDTO(accessToken, refreshToken));
  }
}
