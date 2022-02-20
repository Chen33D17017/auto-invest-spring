package me.peihao.autoInvest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenDTO {
  @JsonProperty("access_token")
  String accessToken;
  @JsonProperty("refresh_token")
  String refreshToken;
}
