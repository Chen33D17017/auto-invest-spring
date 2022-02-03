package me.peihao.autoInvest.repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import me.peihao.autoInvest.constant.ResultInfoConstants;
import me.peihao.autoInvest.exception.AutoInvestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ConfirmationTokenRepository {
  private static final String PREFIX_CONFIRM_TOKEN_KEY = "pc_oneTimeCodePayment_";
  private final RedisTemplate<Object, Object> redisTemplate;
  private final long confirmTokenLiveTime;

  public ConfirmationTokenRepository(
      RedisTemplate<Object, Object> redisTemplate,
      @Value("${auto-invest.token.live-time}") long confirmTokenLiveTimeSec) {
    this.redisTemplate = redisTemplate;
    this.confirmTokenLiveTime = confirmTokenLiveTimeSec;
  }

  public void setConfirmToken(String token, String username) {
    boolean isNotDuplicate = redisTemplate.opsForValue().setIfAbsent(
        PREFIX_CONFIRM_TOKEN_KEY + token,
        username);

    if (!isNotDuplicate) {
      throw new AutoInvestException(ResultInfoConstants.CONFIRM_TOKEN_SET_ERROR);
    }

    redisTemplate.expire(
        PREFIX_CONFIRM_TOKEN_KEY + token,
        confirmTokenLiveTime,
        TimeUnit.SECONDS
    );
  }

  public Optional<String> getUserNameFromConfirmToken(String token) {
    return Optional.ofNullable((String) redisTemplate.opsForValue()
        .get(PREFIX_CONFIRM_TOKEN_KEY + token));
  }
}
