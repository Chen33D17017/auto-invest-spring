package me.peihao.autoInvest.dto.requests;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
  @NotBlank
  private String username;
  @NotBlank
  private String password;
}
