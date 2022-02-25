package me.peihao.autoInvest.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.security.Principal;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.websocket.server.PathParam;

import static me.peihao.autoInvest.common.ResultUtil.generateSuccessResponse;

import me.peihao.autoInvest.dto.requests.PatchUserRequestDTO;
import me.peihao.autoInvest.dto.requests.RefreshTokenDTO;
import me.peihao.autoInvest.dto.response.TokenResponseDTO;
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
  public AppUserController(AppUserService appUserService,
      @Value("jwt.sign-secret") String signSecret) {
    this.appUserService = appUserService;
    this.signSecret = signSecret;
  }

  @PostMapping("/v1/registration")
  public ResponseEntity<String> register(
      @RequestBody RegistrationUserRequestDTO registrationUserRequestDTO) {
    return generateSuccessResponse(appUserService.register(registrationUserRequestDTO));
  }

  @GetMapping("/v1/registration/confirm")
  public ResponseEntity<String> confirmRegistration(@PathParam("token") String token) {
    return generateSuccessResponse(appUserService.confirmToken(token));
  }

  @PatchMapping("/v1/user")
  public ResponseEntity<String> updateUserInfo(Principal principal,
      @RequestBody PatchUserRequestDTO patchUserRequestDTO) {
    return generateSuccessResponse(
        appUserService.patchUser(patchUserRequestDTO, principal.getName()));
  }

  @GetMapping("/v1/user")
  public ResponseEntity<String> getUserInfo(Principal principal) {
    return generateSuccessResponse(appUserService.getUserInfo(principal.getName()));
  }

  @PostMapping("/v1/reissue")
  public ResponseEntity<String> reissueConfirmationToken(
      @PathParam("username") String username) {
    return generateSuccessResponse(
        appUserService.reissueConfirmationToken(username));
  }

  @GetMapping("/v1/refresh/token")
  public ResponseEntity<String> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
    String refreshToken = refreshTokenDTO.getRefreshToken();
    Algorithm algorithm = Algorithm.HMAC256(signSecret.getBytes());
    JWTVerifier verifier = JWT.require(algorithm).build();
    DecodedJWT decodedJWT = verifier.verify(refreshToken);
    String username = decodedJWT.getSubject();
    UserDetails user = appUserService.loadUserByUsername(username);
    String accessToken = JWT.create()
        .withSubject(user.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMicros(10)))
        .withIssuer("RegularInvestDAO")
        .withClaim("roles",
            user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(
                Collectors.toList())).sign(algorithm);
    return generateSuccessResponse(
        TokenResponseDTO.builder().accessToken(accessToken).refreshToken(refreshToken));
  }
}
