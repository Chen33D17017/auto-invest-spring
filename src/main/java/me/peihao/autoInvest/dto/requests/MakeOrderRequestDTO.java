package me.peihao.autoInvest.dto.requests;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MakeOrderRequestDTO {
  String symbol;
  String side;
  String buy_from;
  Float amount;
}
