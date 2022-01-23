package me.peihao.autoInvest.feign;

import me.peihao.autoInvest.dto.feign.requeset.BinanceOrderRequestDTO;
import me.peihao.autoInvest.dto.feign.BinanceTimestampRequestDTO;
import me.peihao.autoInvest.dto.feign.requeset.BinanceFlexibleProductPositionRequestDTO;
import me.peihao.autoInvest.dto.feign.requeset.BinanceRedeemFlexibleProductRequestDTO;
import me.peihao.autoInvest.dto.feign.requeset.BinanceTradeHistoryRequestDTO;
import me.peihao.autoInvest.dto.feign.response.BinanceAccountResponseDTO;
import me.peihao.autoInvest.dto.feign.response.BinanceFlexibleProductPositionResponseDTO;
import me.peihao.autoInvest.dto.feign.response.BinanceOrderResponseDTO;
import me.peihao.autoInvest.dto.feign.response.BinanceTradeHistoryResponseDTO;
import me.peihao.autoInvest.feign.configuration.BinanceFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;


// https://stackoverflow.com/questions/63678878/feign-client-signed-endpoint#63679109
@FeignClient(
    name="binanceFeign",
    url="${binance.baseurl}",
    configuration = BinanceFeignConfiguration.class
)
public interface BinanceFeign {
  @PostMapping(value = "/api/v3/order", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  BinanceOrderResponseDTO newOrder(@SpringQueryMap BinanceOrderRequestDTO orderRequest,
      @RequestHeader("credential") String apiSecret,
      @RequestHeader("X-MBX-APIKEY") String apiKey);

  @GetMapping(value = "/api/v3/account")
  BinanceAccountResponseDTO checkSpotAccountInfo(@SpringQueryMap BinanceTimestampRequestDTO request,
      @RequestHeader("credential") String apiSecret,
      @RequestHeader("X-MBX-APIKEY") String apiKey);

  @GetMapping(value = "/sapi/v1/lending/project/position/list")
  BinanceFlexibleProductPositionResponseDTO getFlexibleProductPosition(
      @SpringQueryMap BinanceFlexibleProductPositionRequestDTO request,
      @RequestHeader("credential") String apiSecret,
      @RequestHeader("X-MBX-APIKEY") String apiKey);

  @PostMapping(value = "/sapi/v1/lending/daily/redeem", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  ResponseEntity<String> redeemFlexibleProduct(
      @SpringQueryMap BinanceRedeemFlexibleProductRequestDTO request,
      @RequestHeader("credential") String apiSecret,
      @RequestHeader("X-MBX-APIKEY") String apiKey);

  @GetMapping(value = "/api/v3/myTrades")
  BinanceTradeHistoryResponseDTO[] getTradeHistory(
      @SpringQueryMap BinanceTradeHistoryRequestDTO request,
      @RequestHeader("credential") String apiSecret,
      @RequestHeader("X-MBX-APIKEY") String apiKey);
}
