package me.peihao.autoInvest.dto.response;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.peihao.autoInvest.constant.WeekDayEnum;
import me.peihao.autoInvest.dto.RegularInvestDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchRegularInvestResponseDTO {
  private List<RegularInvestDTO> regularInvests;
}
