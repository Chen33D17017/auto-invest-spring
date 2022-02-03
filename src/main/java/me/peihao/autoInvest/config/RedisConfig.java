package me.peihao.autoInvest.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.DecoratingStringHashMapper;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;

@Configuration
@EnableRedisRepositories(
    enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP,
    keyspaceNotificationsConfigParameter = ""
)
public class RedisConfig {

  /**
   * RedisTemplate bean
   *
   * @param redisConnectionFactory RedisConnectionFactory
   * @return RedisTemplate
   */
  @Bean
  public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    ObjectMapper objectMapper = this.objectMapper();
    RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
    return redisTemplate;
  }

  /**
   * Create ObjectMapper for redis.
   *
   * @return ObjectMapper
   */
  private ObjectMapper objectMapper() {
    return new ObjectMapper()
        .registerModule(new Jdk8Module())
        .registerModule(new JavaTimeModule())
        .enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
        .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
  }

  /**
   * RedisConnectionFactory bean
   * only in local, we use standalone redis
   *
   * @param redisProperties RedisProperties
   * @return RedisConnectionFactory
   */
  @Bean
  public RedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
    return new JedisConnectionFactory(
        new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort())
    );
  }

  /**
   * ConfigureRedisAction bean for SpringSession with ElasticSearch
   *
   * @return ConfigureRedisAction
   */
  @Bean
  public ConfigureRedisAction configureRedisAction() {
    return ConfigureRedisAction.NO_OP;
  }

  @Bean
  public DecoratingStringHashMapper<Object> jacksonHashMapper() {
    return new DecoratingStringHashMapper<Object>(new Jackson2HashMapper(true));
  }
}