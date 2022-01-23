package me.peihao.autoInvest.dto.feign.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceOrderResponseDTO {
  private String symbol;
  private Integer orderId;
  private String clientOrderId;
  private Float price;
  private Float origQty;
  private Float executedQty;
  private String status;
  private String side;
}
