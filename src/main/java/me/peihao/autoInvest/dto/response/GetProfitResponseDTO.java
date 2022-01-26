package me.peihao.autoInvest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class GetProfitResponseDTO {
  @JsonProperty("crypto_name")
  private String cryptoName;

  private Float amount;

  @JsonProperty("total_cost")
  private Float totalCost;

  @JsonProperty("average_price")
  private Float averagePrice;

  @JsonProperty("profit_rate")
  private String profitRate;
}
