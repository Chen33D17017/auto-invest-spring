package me.peihao.autoInvest.dto.feign;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@Builder
public class BinanceTimestampRequestDTO {

  @NotNull
  Long timestamp;

  @Override
  public String toString(){
    return String.format("timestamp=%s", timestamp);
  }
}
