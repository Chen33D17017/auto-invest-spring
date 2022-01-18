package me.peihao.autoInvest.dto.requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationUserRequestDTO {

  @NotBlank
  private String name;

  @NotBlank
  private String userName;

  @NotNull
  @Email
  private String email;
  @NotBlank
  private String password;
}
