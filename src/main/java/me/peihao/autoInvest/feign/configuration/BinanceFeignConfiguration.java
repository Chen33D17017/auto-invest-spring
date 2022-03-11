package me.peihao.autoInvest.feign.configuration;

import static feign.FeignException.errorStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Logger;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import me.peihao.autoInvest.dto.feign.response.BinanceErrorResponseDTO;
import me.peihao.autoInvest.exception.BinanceFeignException;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

@Slf4j
public class BinanceFeignConfiguration {
  private static ObjectMapper objMapper = new ObjectMapper();

  @Bean
  public Logger.Level logger() {
    return Logger.Level.FULL;
  }

  @Bean
  public Feign.Builder ClientBuilder() {
    return Feign.builder().requestInterceptor(new BinanceRequestInterceptor());
  }

  @Bean
  public ErrorDecoder errorDecoder() {
    return (String methodKey, Response response) -> {
      BinanceErrorResponseDTO errorMessage;
      try {
        errorMessage = objMapper
            .readValue(response.body().toString(), BinanceErrorResponseDTO.class);
      } catch (JsonProcessingException e) {
        log.error("BinanceFeign: Fail to get message {}", e.toString());
        throw new BinanceFeignException("Fail to parsing Binance response");
      }
      throw new BinanceFeignException(errorMessage.getMsg());
    };

  }

  // TODO: Dealing with error case

}
