package me.peihao.autoInvest.dto.feign.requeset;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BinanceRedeemFlexibleProductRequestDTO {
  @NotNull
  private String productId;
  @NotNull
  private Float amount;

  // "FAST", "NORMAL"
  @NotNull
  private String type;

  @NotNull
  private Long timestamp;
}
