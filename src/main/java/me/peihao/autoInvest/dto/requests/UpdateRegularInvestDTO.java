package me.peihao.autoInvest.dto.requests;

import javax.validation.constraints.NotNull;
import lombok.Data;
import me.peihao.autoInvest.validator.Crypto;

@Data
public class UpdateRegularInvestDTO {

    @Crypto
    private String crypto;
    @NotNull
    private String workday;
    @NotNull
    private Float amount;
}
