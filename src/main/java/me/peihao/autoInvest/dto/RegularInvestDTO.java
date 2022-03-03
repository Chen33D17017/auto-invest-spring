package me.peihao.autoInvest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.peihao.autoInvest.validator.Weekday;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegularInvestDTO{
  @NotNull
  @Weekday
  String weekday;

  @NotNull(message="Not allow null")
  String cryptoName;

  @NotNull
  String buyFrom;

  @NotNull
  @Positive
  Float amount;
}