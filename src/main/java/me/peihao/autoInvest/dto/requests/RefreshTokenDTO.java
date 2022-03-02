package me.peihao.autoInvest.dto.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshTokenDTO {

  @NotBlank(message = "Null on Refresh Token")
  private String refreshToken;
}
