package me.peihao.autoInvest.controller;

import javax.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import me.peihao.autoInvest.service.RegistrationUserService;
import me.peihao.autoInvest.dto.requests.RegistrationUserRequestDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
@AllArgsConstructor
public class RegistrationController {

  private final RegistrationUserService registrationUserService;

  @PostMapping("/v1/registration")
  public String register(@RequestBody RegistrationUserRequestDTO registrationUserRequestDTO){
    return registrationUserService.register(registrationUserRequestDTO);
  }

  @GetMapping("/v1/registration/confirm")
  public String confirmRegistration(@PathParam("token") String token){
    return registrationUserService.confirmToken(token);
  }
}
