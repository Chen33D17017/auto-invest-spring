package me.peihao.autoInvest.service;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import me.peihao.autoInvest.constant.AppUserRole;
import me.peihao.autoInvest.constant.ResultInfoConstants;
import me.peihao.autoInvest.dto.feign.requeset.DiscordMessageRequestDTO;
import me.peihao.autoInvest.dto.requests.PatchUserRequestDTO;
import me.peihao.autoInvest.dto.requests.RegistrationUserRequestDTO;
import me.peihao.autoInvest.dto.response.GetUserResponseDTO;
import me.peihao.autoInvest.dto.response.PatchUserResponseDTO;
import me.peihao.autoInvest.dto.response.RegistrationUserResponseDTO;
import me.peihao.autoInvest.exception.AutoInvestException;
import me.peihao.autoInvest.feign.DiscordFeign;
import me.peihao.autoInvest.model.AppUser;
import me.peihao.autoInvest.repository.AppUserRepository;
import me.peihao.autoInvest.repository.ConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AppUserService implements UserDetailsService {

  private final static String USER_NOT_FOUND = "username %s not found";
  private final AppUserRepository appUserRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final ConfirmationTokenRepository confirmationTokenRepository;
  private final DiscordFeign discordFeign;
  private final String adminWebhookId;
  private final int markSpec;

  @Autowired
  public AppUserService(
      AppUserRepository appUserRepository,
      BCryptPasswordEncoder bCryptPasswordEncoder,
      ConfirmationTokenRepository confirmationTokenRepository,
      DiscordFeign discordFeign,
      @Value("${webhook.admin-id}") String adminWebhookId,
      @Value("${auto-invest.mark-spec}") int markSpec
  ) {
    this.appUserRepository = appUserRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.confirmationTokenRepository = confirmationTokenRepository;
    this.discordFeign = discordFeign;
    this.adminWebhookId = adminWebhookId;
    this.markSpec = markSpec;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return appUserRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, username)));
  }

  @Transactional
  public RegistrationUserResponseDTO signUpUser(AppUser appUser) throws Exception {
    boolean userExists = appUserRepository.findByUsername(appUser.getUsername())
        .isPresent();
    if (userExists) {
      throw new IllegalStateException("username already taken");
    }

    String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
    appUser.setPassword(encodedPassword);
    appUserRepository.save(appUser);

    String token = UUID.randomUUID().toString();

    confirmationTokenRepository.setConfirmToken(token, appUser.getUsername());

    discordFeign.sendWebhook(adminWebhookId, new DiscordMessageRequestDTO(
        String.format("%s register with confirmation token %s", appUser.getUsername(), token)));


    return RegistrationUserResponseDTO.builder()
        .username(appUser.getUsername())
        .name(appUser.getName())
        .email(appUser.getEmail())
        .apiKey(maskString(appUser.getApiKey()))
        .apiSecret(maskString(appUser.getApiSecret()))
        .confirmToken(token)
        .build();
  }

  public RegistrationUserResponseDTO register(RegistrationUserRequestDTO requestDTO)
      throws Exception {
    return signUpUser(
        new AppUser(
            requestDTO.getName(),
            requestDTO.getUsername(),
            requestDTO.getEmail(),
            requestDTO.getPassword(),
            requestDTO.getApiKey(),
            requestDTO.getApiSecret(),
            AppUserRole.USER
        )
    );
  }

  public RegistrationUserResponseDTO reissueConfirmationToken(String username) {
    AppUser targetUser = appUserRepository.findByUsername(username).orElseThrow(
        () -> new IllegalStateException("User not found")
    );
    String token = UUID.randomUUID().toString();
    confirmationTokenRepository.setConfirmToken(token, username);

    return RegistrationUserResponseDTO.builder()
        .username(targetUser.getUsername())
        .name(targetUser.getName())
        .email(targetUser.getEmail())
        .confirmToken(token)
        .build();
  }

  public String confirmToken(String token) {
    String username = confirmationTokenRepository.getUserNameFromConfirmToken(token)
        .orElseThrow(() -> new AutoInvestException(ResultInfoConstants.CONFIRM_TOKEN_ERROR));
    AppUser targetUser = appUserRepository.findByUsername(username).orElseThrow(
        () -> new AutoInvestException(ResultInfoConstants.CONFIRM_TOKEN_ERROR)
    );
    targetUser.setEnabled(true);
    appUserRepository.save(targetUser);
    return "confirm";
  }

  public PatchUserResponseDTO patchUser(PatchUserRequestDTO requestDTO, String username)
      throws Exception {
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
    return PatchUserResponseDTO.builder().username(appUser.getUsername()).email(appUser.getEmail())
        .name(appUser.getName()).apiKey(maskString(appUser.getApiKey())).apiSecret(maskString(appUser.getApiSecret())).build();
  }

  public GetUserResponseDTO getUserInfo(String username) throws Exception {
    AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(
        () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, username)));
    return GetUserResponseDTO.generateUserResponseDTO(appUser, maskString(appUser.getApiKey()), maskString(appUser.getApiSecret()));
  }

  private String maskString(String strText) throws Exception {
    return maskString(strText, markSpec, strText.length() - markSpec, '*');
  }

  private String maskString(String strText, int start, int end, char maskChar)
      throws Exception{

    if(strText == null || strText.equals(""))
      return "";

    if(start < 0)
      start = 0;

    if( end > strText.length() )
      end = strText.length();

    if(start > end)
      throw new Exception("End index cannot be greater than start index");

    int maskLength = end - start;

    if(maskLength == 0)
      return strText;

    StringBuilder sbMaskString = new StringBuilder(maskLength);

    for(int i = 0; i < maskLength; i++){
      sbMaskString.append(maskChar);
    }

    return strText.substring(0, start)
        + sbMaskString.toString()
        + strText.substring(start + maskLength);
  }
}
