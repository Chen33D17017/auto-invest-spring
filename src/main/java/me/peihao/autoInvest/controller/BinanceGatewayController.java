package me.peihao.autoInvest.controller;


import java.sql.Timestamp;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.peihao.autoInvest.dto.feign.requeset.BinanceOrderRequestDTO;
import me.peihao.autoInvest.dto.feign.BinanceTimestampRequestDTO;
import me.peihao.autoInvest.dto.feign.requeset.BinanceTradeHistoryRequestDTO;
import me.peihao.autoInvest.feign.BinanceFeign;
import me.peihao.autoInvest.model.AppUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static me.peihao.autoInvest.common.ResultUtil.generateSuccessResponse;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
@Slf4j
public class BinanceGatewayController {

  final private BinanceFeign binanceFeign;


  @PostMapping("/v1/order")
  public ResponseEntity<String> makeOrder(
      @AuthenticationPrincipal AppUser appUser,
      @Valid @RequestBody BinanceOrderRequestDTO binanceOrderRequestDTO){
    return generateSuccessResponse(binanceOrderRequestDTO);
  }

  @GetMapping("/v1/status")
  public ResponseEntity<String> checkStatus(
      @AuthenticationPrincipal AppUser appUser) {
    Long timestamp = new Timestamp(System.currentTimeMillis()).getTime();
    BinanceTimestampRequestDTO requestQuery = new BinanceTimestampRequestDTO(timestamp);
    return generateSuccessResponse(
        binanceFeign.checkSpotAccountInfo(requestQuery, appUser.getApiSecret(),
            appUser.getApiKey()));
  }

  @GetMapping("/v1/history")
  public ResponseEntity<String> fetchHistory(
      @AuthenticationPrincipal AppUser appUser,
      @RequestParam(required = false, name = "symbol") String symbol){
    Long timestamp = new Timestamp(System.currentTimeMillis()).getTime();
    BinanceTradeHistoryRequestDTO requestQuery = new BinanceTradeHistoryRequestDTO(symbol, timestamp);
    return generateSuccessResponse(
        binanceFeign.getTradeHistory(requestQuery, appUser.getApiSecret(),
            appUser.getApiKey()));
  }
}
