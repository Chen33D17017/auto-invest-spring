package me.peihao.autoInvest.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import me.peihao.autoInvest.model.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

  // TODO: Research about JPA & add query
  Optional<ConfirmationToken> findByToken(String token);

  // TODO: check @modifying
  @Transactional
  @Modifying
  @Query("UPDATE ConfirmationToken c " +
      "SET c.confirmedAt = ?2 " +
      "WHERE c.token = ?1")
  void setConfirmedAtByToken(String token, LocalDateTime confirmedAt);
}
