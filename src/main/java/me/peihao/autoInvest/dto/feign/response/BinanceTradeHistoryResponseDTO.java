package me.peihao.autoInvest.dto.feign.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceTradeHistoryResponseDTO {
  private String symbol;
  private Integer Id;
  private Integer orderId;
  private Float price;
  private Float qty;
  private Long time;
  private Boolean isBuyer;
}
