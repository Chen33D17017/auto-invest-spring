package me.peihao.autoInvest.dto.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PutRegularInvestResponseDTO {

    private String cryptoName;

    private List<String> weekdays;

    private String buyFrom;

    private Float amount;
}
