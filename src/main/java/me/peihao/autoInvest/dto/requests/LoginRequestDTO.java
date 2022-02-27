package me.peihao.autoInvest.dto.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequestDTO {
  @NotBlank
  private String username;
  @NotBlank
  private String password;
  private Boolean rememberMe = false;
}
