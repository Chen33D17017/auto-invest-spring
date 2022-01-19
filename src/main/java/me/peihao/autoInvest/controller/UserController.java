package me.peihao.autoInvest.controller;

import java.security.Principal;
import javax.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import me.peihao.autoInvest.common.ResultUtil;
import static me.peihao.autoInvest.common.ResultUtil.generateSuccessResponse;
import me.peihao.autoInvest.constant.ResultInfoConstants;
import me.peihao.autoInvest.model.AppUser;
import me.peihao.autoInvest.service.RegistrationUserService;
import me.peihao.autoInvest.dto.requests.RegistrationUserRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
@AllArgsConstructor
public class UserController {

  private final RegistrationUserService registrationUserService;

  @PostMapping("/v1/registration")
  public ResponseEntity<String> register(
      @RequestBody RegistrationUserRequestDTO registrationUserRequestDTO) {
    return generateSuccessResponse(registrationUserService.register(registrationUserRequestDTO));
  }

  @GetMapping("/v1/registration/confirm")
  public ResponseEntity<String> confirmRegistration(@PathParam("token") String token){
    return generateSuccessResponse(registrationUserService.confirmToken(token));
  }

  @PatchMapping("/v1/user")
  public ResponseEntity<String> updateUserInfo(Principal principal){
    return generateSuccessResponse(principal.getName());
  }
}
