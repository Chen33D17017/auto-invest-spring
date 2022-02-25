package me.peihao.autoInvest.dto.response;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.peihao.autoInvest.constant.WeekDayEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchRegularInvestResponseDTO {
  private List<RegularInvest> regularInvests;

  @Data
  @AllArgsConstructor
  public static class RegularInvest {
    WeekDayEnum weekdays;
    String buyFrom;
    String cryptoName;
    Float amount;
  }

}
