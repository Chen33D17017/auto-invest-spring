package me.peihao.autoInvest.feign.configuration;

import feign.Feign;
import feign.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

@Slf4j
public class BinanceFeignConfiguration {

  @Bean
  public Logger.Level logger() {
    return Logger.Level.FULL;
  }

  @Bean
  public Feign.Builder ClientBuilder() {
    return Feign.builder().requestInterceptor(new BinanceRequestInterceptor());
  }

  // TODO: Dealing with error case

}
