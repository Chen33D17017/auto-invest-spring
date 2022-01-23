package me.peihao.autoInvest.dto.feign.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceFlexibleProductPositionResponseDTO {
  private String asset;
  private String productId;
  private String productName;
  private Float freeAmount;
}
