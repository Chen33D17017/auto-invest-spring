package me.peihao.autoInvest.dto.feign.requeset;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BinanceFlexibleProductPositionRequestDTO {
  private String asset;
  private Long timestamp;
}
