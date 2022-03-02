package me.peihao.autoInvest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;
import me.peihao.autoInvest.validator.Weekday;

@Data
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