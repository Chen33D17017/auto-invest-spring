package me.peihao.autoInvest.controller;

import java.security.Principal;
import javax.websocket.server.PathParam;
import lombok.AllArgsConstructor;

import static me.peihao.autoInvest.common.ResultUtil.generateSuccessResponse;

import me.peihao.autoInvest.dto.requests.PatchUserRequestDTO;
import me.peihao.autoInvest.feign.DiscordFeign;
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
@RequestMapping(path = "/api")
@AllArgsConstructor
public class AppUserController {

  private final AppUserService appUserService;

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

  @PostMapping
  public ResponseEntity<String> reissueConfirmationToken(
      Principal principal){
    return generateSuccessResponse(
        appUserService.reissueConfirmationToken(principal.getName()));
  }
}
