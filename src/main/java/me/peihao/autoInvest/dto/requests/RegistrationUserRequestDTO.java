package me.peihao.autoInvest.dto.requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Value;

@Value
public class RegistrationUserRequestDTO {

  @NotBlank
  String name;

  @NotBlank
  String userName;

  @NotNull
  @Email
  String email;
  @NotBlank
  String password;

  @NotNull
  String apiKey;

  @NotNull
  String apiSecret;
}
