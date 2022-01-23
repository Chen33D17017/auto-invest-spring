package me.peihao.autoInvest.dto.feign.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceAccountResponseDTO {

  private BalanceInfo[] balances;

  @Data
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class BalanceInfo{
    private String asset;
    private Float free;
    private Float locked;
  }
}
