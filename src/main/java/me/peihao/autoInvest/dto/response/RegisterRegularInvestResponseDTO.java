package me.peihao.autoInvest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegisterRegularInvestResponseDTO {

  private List<String> weekdays;

  @JsonProperty("buy_from")
  private String buyFrom;

  @JsonProperty("crypto_name")
  private String cryptoName;

  private Float amount;
}
