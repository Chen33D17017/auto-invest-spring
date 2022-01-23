package me.peihao.autoInvest.dto.feign.requeset;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BinanceOrderRequestDTO{
  @NotNull
  String symbol;
  @NotNull
  String side;
  @NotNull
  String type;

  String timeInForce;
  Float quantity;
  Float quoteOrderQty;
  Float price;
  String newClientOrderId;
  Float stopPrice;
  Float icebergQty;
  String newOrderRespType;
  Long recvWindow;

  @NotNull
  Long timestamp;
}
