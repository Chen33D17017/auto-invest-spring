package me.peihao.autoInvest.service;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import me.peihao.autoInvest.model.ConfirmationToken;
import me.peihao.autoInvest.repository.ConfirmationTokenRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

  private final ConfirmationTokenRepository confirmationTokenRepository;

  public void saveConfirmationToken(ConfirmationToken token){
    confirmationTokenRepository.save(token);
  }

  public Optional<ConfirmationToken> getConfirmationToken(String token){
    return confirmationTokenRepository.findByToken(token);
  }

  public void setConfirmedAtByToken(String token){
    confirmationTokenRepository.setConfirmedAtByToken(token, LocalDateTime.now());
  }
}
