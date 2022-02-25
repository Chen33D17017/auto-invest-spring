package me.peihao.autoInvest.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MakeOrderRequestDTO {
  String symbol;
  String side;
  String buyFrom;
  Float amount;
}
