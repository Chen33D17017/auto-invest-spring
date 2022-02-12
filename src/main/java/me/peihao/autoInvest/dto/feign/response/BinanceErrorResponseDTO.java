package me.peihao.autoInvest.dto.feign.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BinanceErrorResponseDTO {
  private int code;
  private String msg;
}
