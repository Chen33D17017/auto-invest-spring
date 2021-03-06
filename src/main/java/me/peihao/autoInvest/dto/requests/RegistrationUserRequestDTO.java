package me.peihao.autoInvest.dto.requests;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Value;

@Value
public class RegistrationUserRequestDTO {

  @NotBlank
  String name;

  @NotBlank
  String username;

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
