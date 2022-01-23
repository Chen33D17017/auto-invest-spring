package me.peihao.autoInvest.dto.feign.requeset;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BinanceTradeHistoryRequestDTO {

  public BinanceTradeHistoryRequestDTO(@NotNull String symbol, Long timestamp) {
    this.symbol = symbol;
    this.timestamp = timestamp;
  }

  @NotNull
  private String symbol;

  private Long orderId;
  private Long startTime;
  private Long endTime;
  private Long fromId;
  private Integer limit;
  private Long timestamp;
}
