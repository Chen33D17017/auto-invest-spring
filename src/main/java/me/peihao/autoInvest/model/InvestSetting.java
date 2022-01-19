package me.peihao.autoInvest.model;

import javax.persistence.CascadeType;
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
import lombok.Data;
import lombok.NoArgsConstructor;
import me.peihao.autoInvest.constant.WeekDayEnum;

// ref: https://medium.com/@andhikayusup/spring-boot-guide-for-beginner-entity-relationship-65fd614f07c6

@Data
@NoArgsConstructor
@Entity
public class InvestSetting {
  @SequenceGenerator(
      name = "investSetting_sequence",
      sequenceName = "investSetting_sequence",
      allocationSize = 1
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "investSetting_sequence"
  )
  @Id
  private Long id;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_user_InvestSetting"), name = "user_id", referencedColumnName ="id", nullable = false)
  private AppUser appUser;

  // https://www.baeldung.com/jpa-persisting-enums-in-jpa
  @Enumerated(EnumType.STRING)
  private WeekDayEnum weekDay;
  private String cryptoName;
}
