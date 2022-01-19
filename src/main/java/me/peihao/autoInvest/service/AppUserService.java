package me.peihao.autoInvest.service;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import me.peihao.autoInvest.dto.response.RegistrationUserResponseDTO;
import me.peihao.autoInvest.model.AppUser;
import me.peihao.autoInvest.model.ConfirmationToken;
import me.peihao.autoInvest.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

  private final static String USER_NOT_FOUND = "user with email %s not found";
  private final AppUserRepository appUserRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final ConfirmationTokenService confirmationTokenService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return appUserRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, username)));
  }

  @Transactional
  public RegistrationUserResponseDTO signUpUser(AppUser appUser){
    boolean userExists = appUserRepository.findByUsername(appUser.getUsername())
        .isPresent();
    if (userExists) {
      throw new IllegalStateException("userName already taken");
    }

    String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
    appUser.setPassword(encodedPassword);
    appUserRepository.save(appUser);

    String token = UUID.randomUUID().toString();

    ConfirmationToken confirmationToken = new ConfirmationToken(
        token,
        LocalDateTime.now(),
        LocalDateTime.now().plusMinutes(15),
        appUser
    );

    confirmationTokenService.saveConfirmationToken(confirmationToken);

    // TODO: send token by discord

    RegistrationUserResponseDTO responseDTO = RegistrationUserResponseDTO.builder()
        .userName(appUser.getName())
        .name(appUser.getName())
        .email(appUser.getEmail())
        .confirmToken(confirmationToken.getToken())
        .build();
    return responseDTO;
  }
}
