package me.peihao.autoInvest.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Value;

@Value
public class RegisterRegularInvestRequestDTO {

    @NotNull
    @JsonProperty("weekdays")
    List<String> weekdays;

    @NotNull(message = "This field cannot be null")
    @JsonProperty("crypto_name")
    String cryptoName;

    @NotNull
    @JsonProperty("buy_from")
    String buyFrom;

    @NotNull
    @Positive
    Float amount;
}
