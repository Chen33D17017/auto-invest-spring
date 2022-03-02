package me.peihao.autoInvest.service;


import static java.util.Arrays.stream;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import me.peihao.autoInvest.constant.ResultInfoConstants;
import me.peihao.autoInvest.dto.response.TokenResponseDTO;
import me.peihao.autoInvest.exception.AutoInvestException;
import me.peihao.autoInvest.model.AppUser;
import me.peihao.autoInvest.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

  private final long accessTokenLiveTime;
  private final long refreshTokenLiveTime;
  private final long rememberMeLiveTime;
  private final Algorithm algorithm;
  private final AppUserRepository appUserRepository;

  @Autowired
  public TokenService(
      @Value("${token.access-token.live-time-minutes}") long accessTokenLiveTime,
      @Value("${token.refresh-token.live-time-minutes}") long refreshTokenLiveTime,
      @Value("${token.remember-me.live-time-minutes}") long rememberMeLiveTime,
      @Value("${jwt.sign-secret}") String signSecret,
      AppUserRepository appUserRepository){
    this.accessTokenLiveTime = accessTokenLiveTime;
    this.refreshTokenLiveTime = refreshTokenLiveTime;
    this.rememberMeLiveTime = rememberMeLiveTime;
    this.algorithm = Algorithm.HMAC256(signSecret.getBytes());
    this.appUserRepository = appUserRepository;
  }

  public TokenResponseDTO generateJWTResponse(AppUser user, Boolean rememberMe){
    long tokenLiveTime = rememberMe ? rememberMeLiveTime : refreshTokenLiveTime;

    String refreshToken = JWT.create()
        .withSubject(user.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(tokenLiveTime)))
        .withIssuer("RegularInvestDAO").sign(algorithm);

    return generateJWTResponse(user, refreshToken);
  }

  public String generateAccessToken(AppUser user) {
    return JWT.create()
        .withSubject(user.getUsername())
        .withExpiresAt(
            new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(accessTokenLiveTime)))
        .withIssuer("RegularInvestDAO")
        .withClaim("roles",
            user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(
                Collectors.toList())).sign(algorithm);
  }

  public TokenResponseDTO generateJWTResponse(AppUser user, String refreshToken){
    String access_token = generateAccessToken(user);
    return TokenResponseDTO.builder().accessToken(access_token).refreshToken(refreshToken)
        .build();
  }

  public TokenResponseDTO verifyAndRegenerateTokenResponse(String refreshToken){
    if(refreshToken == null){
      throw new AutoInvestException(ResultInfoConstants.INVALID_TOKEN);
    }
    JWTVerifier verifier = JWT.require(algorithm).build();
    DecodedJWT decodedJWT = verifier.verify(refreshToken);
    String username = decodedJWT.getSubject();
    AppUser user = appUserRepository.findByUsername(username).orElseThrow(
        () -> new UsernameNotFoundException(String.format("User Not found %s", username)));
    return generateJWTResponse(user, refreshToken);
  }

  public UsernamePasswordAuthenticationToken authenticationToken(String accessToken){
    JWTVerifier verifier = JWT.require(algorithm).build();
    DecodedJWT decodedJWT = verifier.verify(accessToken);
    String username = decodedJWT.getSubject();
    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
    stream(roles).forEach(role -> {
      authorities.add(new SimpleGrantedAuthority(role));
    });
    return new UsernamePasswordAuthenticationToken(
        username, null, authorities);
  }
}
