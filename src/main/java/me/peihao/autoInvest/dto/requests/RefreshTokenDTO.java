package me.peihao.autoInvest.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RefreshTokenDTO {

  @JsonProperty("refresh_token")
  private String refreshToken;
}
