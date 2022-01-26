package me.peihao.autoInvest.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.peihao.autoInvest.constant.WeekDayEnum;

// ref: https://medium.com/@andhikayusup/spring-boot-guide-for-beginner-entity-relationship-65fd614f07c6

@Data
@NoArgsConstructor
@Table(name = "regular_invest", uniqueConstraints = {
    @UniqueConstraint(name = "UniqueUserIdAndCryptoName", columnNames = { "user_id", "crypto_name", "weekday" })})
@Entity
public class RegularInvest {

  public RegularInvest(AppUser appUser, WeekDayEnum weekday, String buyFrom, String cryptoName, Float amount){
    this.appUser = appUser;
    this.weekday = weekday;
    this.buyFrom = buyFrom;
    this.cryptoName = cryptoName;
    this.amount = amount;
    this.isEnable = true;
  }

  @SequenceGenerator(
      name = "invest_setting_sequence",
      sequenceName = "invest_setting_sequence",
      allocationSize = 1
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "invest_setting_sequence"
  )
  @Id
  private Long id;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_user_InvestSetting"), name = "user_id", referencedColumnName ="id", nullable = false)
  private AppUser appUser;

  // https://www.baeldung.com/jpa-persisting-enums-in-jpa
  @Enumerated(EnumType.STRING)
  @Column(name = "weekday")
  private WeekDayEnum weekday;

  @Column(name = "crypto_name")
  private String cryptoName;

  @Column(name = "buy_from")
  private String buyFrom;

  private Float amount;
  private boolean isEnable;

  @Column(name = "smart_buy", nullable = false)
  private boolean smartBuy = false;
}
