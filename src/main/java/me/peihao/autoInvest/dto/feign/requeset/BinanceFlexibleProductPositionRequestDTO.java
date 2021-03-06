package me.peihao.autoInvest.dto.feign.requeset;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BinanceFlexibleProductPositionRequestDTO {

  public BinanceFlexibleProductPositionRequestDTO(String asset) {
    this.asset = asset;
    this.timestamp = new Timestamp(System.currentTimeMillis()).getTime();
  }

  private String asset;
  private Long timestamp;
}
