package me.peihao.autoInvest.model;

import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;

// should be migrate to redis
@Data
@NoArgsConstructor
@Entity
public class ConfirmationToken {

  @SequenceGenerator(
      name = "confirmation_token_sequence",
      sequenceName = "confirmation_token_sequence",
      allocationSize = 1
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "confirmation_token_sequence"
  )
  @Id
  private Long id;
  private String token;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime expiredAt;

  private LocalDateTime confirmedAt;

  @ManyToOne
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_user_confirmToken"), name = "user_id", referencedColumnName ="id", nullable = false)
  private AppUser appUser;

  public ConfirmationToken(String token, LocalDateTime createdAt, LocalDateTime expiredAt,
       AppUser appUser) {
    this.token = token;
    this.createdAt = createdAt;
    this.expiredAt = expiredAt;
    this.appUser = appUser;
  }
}
