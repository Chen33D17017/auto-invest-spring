package me.peihao.autoInvest.service;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import me.peihao.autoInvest.constant.AppUserRole;
import me.peihao.autoInvest.dto.requests.RegistrationUserRequestDTO;
import me.peihao.autoInvest.model.AppUser;
import me.peihao.autoInvest.model.ConfirmationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RegistrationUserService {

  private final AppUserService appUserService;
  private final ConfirmationTokenService confirmationTokenService;

  public String register(RegistrationUserRequestDTO requestDTO) {
    return appUserService.signUpUser(
        new AppUser(
            requestDTO.getName(),
            requestDTO.getUserName(),
            requestDTO.getEmail(),
            requestDTO.getPassword(),
            AppUserRole.USER
        )
    );
  }

  @Transactional
  public String confirmToken(String token){
    ConfirmationToken confirmationToken = confirmationTokenService.getConfirmationToken(token).orElseThrow(
        () -> new IllegalStateException("token not found"));
    if (confirmationToken.getConfirmedAt() != null){
      throw new IllegalStateException("email already confirmed");
    }

    LocalDateTime expiredAt = confirmationToken.getExpiredAt();

    if(expiredAt.isBefore(LocalDateTime.now())){
      throw new IllegalStateException("token expired");
    }

    confirmationTokenService.setConfirmedAtByToken(token);
    confirmationToken.getAppUser().setEnabled(true);
    return "confirm";
  }
}
