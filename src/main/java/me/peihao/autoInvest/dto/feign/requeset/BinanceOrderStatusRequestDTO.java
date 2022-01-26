package me.peihao.autoInvest.dto.feign.requeset;

import java.sql.Timestamp;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BinanceOrderStatusRequestDTO {
  public BinanceOrderStatusRequestDTO(String symbol, Long orderId){
    this.symbol = symbol;
    this.orderId = orderId;
    this.timestamp = new Timestamp(System.currentTimeMillis()).getTime();
  }
  @NotNull
  private String symbol;
  @NotNull
  private Long orderId;
  private Long timestamp;
}
