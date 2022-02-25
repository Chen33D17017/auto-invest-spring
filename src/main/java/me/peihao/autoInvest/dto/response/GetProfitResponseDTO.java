package me.peihao.autoInvest.dto.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class GetProfitResponseDTO {
  private String cryptoName;

  private Float amount;

  private Float totalCost;

  private Float averagePrice;

  private String profitRate;

  private Float priceNow;
}
