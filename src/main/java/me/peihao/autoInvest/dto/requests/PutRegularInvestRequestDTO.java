package me.peihao.autoInvest.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.peihao.autoInvest.validator.Crypto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PutRegularInvestRequestDTO {

    @NotNull
    private List<String> weekdays;

    @NotNull
    private String buyFrom;

    @NotNull
    private Float amount;
}
