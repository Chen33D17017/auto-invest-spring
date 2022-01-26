package me.peihao.autoInvest.service;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import me.peihao.autoInvest.constant.AppUserRole;
import me.peihao.autoInvest.dto.requests.PatchUserRequestDTO;
import me.peihao.autoInvest.dto.requests.RegistrationUserRequestDTO;
import me.peihao.autoInvest.dto.response.GetUserResponseDTO;
import me.peihao.autoInvest.dto.response.PatchUserResponseDTO;
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


    return RegistrationUserResponseDTO.builder()
        .userName(appUser.getUsername())
        .name(appUser.getName())
        .email(appUser.getEmail())
        .confirmToken(confirmationToken.getToken())
        .build();
  }

  public RegistrationUserResponseDTO register(RegistrationUserRequestDTO requestDTO) {
    return signUpUser(
        new AppUser(
            requestDTO.getName(),
            requestDTO.getUserName(),
            requestDTO.getEmail(),
            requestDTO.getPassword(),
            requestDTO.getApiKey(),
            requestDTO.getApiSecret(),
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

  public PatchUserResponseDTO patchUser(PatchUserRequestDTO requestDTO, String username) {
    AppUser appUser = appUserRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, username)));

    if (requestDTO.getName() != null && requestDTO.getName().length() > 0) {
      appUser.setName(requestDTO.getName());
    }

    if (requestDTO.getEmail() != null && requestDTO.getEmail().length() > 0) {
      appUser.setEmail(requestDTO.getEmail());
    }

    if (requestDTO.getPassword() != null && requestDTO.getPassword().length() > 0) {
      appUser.setPassword(requestDTO.getPassword());
    }

    if (requestDTO.getApiKey() != null && requestDTO.getApiKey().length() > 0) {
      appUser.setApiKey(requestDTO.getApiKey());
    }

    if (requestDTO.getApiSecret() != null && requestDTO.getApiSecret().length() > 0) {
      appUser.setApiSecret(requestDTO.getApiSecret());
    }


    appUserRepository.save(appUser);
    return PatchUserResponseDTO.builder().userName(appUser.getUsername()).email(appUser.getEmail())
        .name(appUser.getName()).build();
  }

  public GetUserResponseDTO getUserInfo(String username) {
    return GetUserResponseDTO.generateUserResponseDTO(
        appUserRepository.findByUsername(username).orElseThrow(
            () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, username)))
    );
  }
}
