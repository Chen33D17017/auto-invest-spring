package me.peihao.autoInvest.model;

import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class TradeHistory {

  @Column(name = "id")
  private @Id Long orderId;

  private String symbol;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_tradeLog_user"), name = "user_id", referencedColumnName ="id", nullable = false)
  private AppUser appUser;

  private Float price;
  private Float amount;
  private String side;
  private LocalDateTime time;
}
