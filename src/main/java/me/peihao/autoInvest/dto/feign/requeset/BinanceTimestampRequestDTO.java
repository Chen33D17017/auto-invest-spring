package me.peihao.autoInvest.dto.feign.requeset;

import java.sql.Timestamp;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BinanceTimestampRequestDTO {

  public BinanceTimestampRequestDTO(){
    this.timestamp = new Timestamp(System.currentTimeMillis()).getTime();
  }

  @NotNull
  Long timestamp;
}
