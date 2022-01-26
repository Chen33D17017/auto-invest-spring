package me.peihao.autoInvest.dto.feign.requeset;

import java.sql.Timestamp;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BinanceRedeemFlexibleProductRequestDTO {

  public BinanceRedeemFlexibleProductRequestDTO(
      @NotNull String productId, @NotNull Float amount) {
    this.productId = productId;
    this.amount = amount;
    this.type = "FAST";
    this.timestamp = new Timestamp(System.currentTimeMillis()).getTime();
  }

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
