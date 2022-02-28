package me.peihao.autoInvest.controller;

import java.security.Principal;
import javax.websocket.server.PathParam;

import static me.peihao.autoInvest.common.ResultUtil.generateSuccessResponse;

import lombok.AllArgsConstructor;
import me.peihao.autoInvest.service.TokenService;
import me.peihao.autoInvest.dto.requests.PatchUserRequestDTO;
import me.peihao.autoInvest.dto.requests.RefreshTokenDTO;
import me.peihao.autoInvest.service.AppUserService;
import me.peihao.autoInvest.dto.requests.RegistrationUserRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api")
public class AppUserController {

  private final AppUserService appUserService;
  private final TokenService tokenService;

  @PostMapping("/v1/registration")
  public ResponseEntity<String> register(
      @RequestBody RegistrationUserRequestDTO registrationUserRequestDTO) throws Exception {
    return generateSuccessResponse(appUserService.register(registrationUserRequestDTO));
  }

  @GetMapping("/v1/registration/confirm")
  public ResponseEntity<String> confirmRegistration(@PathParam("token") String token) {
    return generateSuccessResponse(appUserService.confirmToken(token));
  }

  @PatchMapping("/v1/user")
  public ResponseEntity<String> updateUserInfo(Principal principal,
      @RequestBody PatchUserRequestDTO patchUserRequestDTO) throws Exception {
    return generateSuccessResponse(
        appUserService.patchUser(patchUserRequestDTO, principal.getName()));
  }

  @GetMapping("/v1/user")
  public ResponseEntity<String> getUserInfo(Principal principal) throws Exception {
    return generateSuccessResponse(appUserService.getUserInfo(principal.getName()));
  }

  @PostMapping("/v1/reissue")
  public ResponseEntity<String> reissueConfirmationToken(
      @PathParam("username") String username) {
    return generateSuccessResponse(
        appUserService.reissueConfirmationToken(username));
  }

  @PostMapping("/v1/refresh/token")
  public ResponseEntity<String> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
    return generateSuccessResponse(tokenService.verifyAndRegenerateTokenResponse(refreshTokenDTO.getRefreshToken()));
  }
}
