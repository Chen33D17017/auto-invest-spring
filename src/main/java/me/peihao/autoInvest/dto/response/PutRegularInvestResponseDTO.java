package me.peihao.autoInvest.dto.response;

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

    @JsonProperty("crypto_name")
    private String cryptoName;

    @JsonProperty("weekdays")
    private List<String> weekdays;

    @JsonProperty("buy_from")
    private String buyFrom;

    private Float amount;
}
