package me.peihao.autoInvest.dto.feign.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceTradeHistoryResponseDTO {
  private String symbol;
  private Long Id;
  private Long orderId;
  private Float price;
  private Float qty;
  private Long time;
  private Boolean isBuyer;
}
