package me.peihao.autoInvest.dto.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegisterRegularInvestResponseDTO {

  private List<String> weekdays;

  private String buyFrom;

  private String cryptoName;

  private Float amount;
}
