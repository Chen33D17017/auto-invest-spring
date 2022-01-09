package me.peihao.autoInvest.dto.requests;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;
import me.peihao.autoInvest.validator.Weekday;

@Data
public class RegisterRegularInvestDTO {

    @NotNull
    @Weekday
    private String workDay;

    @NotNull(message = "This field cannot be null")
    private String currency;

    @NotNull
    @Positive
    private float amount;
}
