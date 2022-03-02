package me.peihao.autoInvest.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;
import lombok.Value;
import me.peihao.autoInvest.dto.RegularInvestDTO;
import me.peihao.autoInvest.validator.Weekday;
import me.peihao.autoInvest.validator.WhenDuplicate;

@Value
public class RegisterRegularInvestRequestDTO {

    @NotNull
    RegularInvestDTO[] regularInvests;

    @WhenDuplicate
    String whenDuplicate;


}
