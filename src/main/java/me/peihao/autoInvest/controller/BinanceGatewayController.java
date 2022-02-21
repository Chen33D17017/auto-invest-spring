package me.peihao.autoInvest.controller;


import java.security.Principal;
import java.sql.Timestamp;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.peihao.autoInvest.dto.feign.requeset.BinanceOrderRequestDTO;
import me.peihao.autoInvest.dto.feign.requeset.BinanceOrderStatusRequestDTO;
import me.peihao.autoInvest.dto.feign.requeset.BinanceTimestampRequestDTO;
import me.peihao.autoInvest.dto.feign.requeset.BinanceTradeHistoryRequestDTO;
import me.peihao.autoInvest.dto.requests.MakeOrderRequestDTO;
import me.peihao.autoInvest.feign.BinanceFeign;
import me.peihao.autoInvest.model.AppUser;
import me.peihao.autoInvest.repository.AppUserRepository;
import me.peihao.autoInvest.service.BinanceGatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

  final private BinanceGatewayService binanceGatewayService;
  final private AppUserRepository appUserRepository;
  final private BinanceFeign binanceFeign;


  // Order to Binance Directly
  @PostMapping("/v1/order")
  public ResponseEntity<String> makeOrder(
      Principal principal,
      @Valid @RequestBody BinanceOrderRequestDTO binanceOrderRequestDTO) {
    AppUser appUser = getAppUser(principal.getName());
    return generateSuccessResponse(
        binanceFeign.newOrder(binanceOrderRequestDTO, appUser.getApiSecret(),
            appUser.getApiKey()));
  }

  // Order to Binance and save trading log to db
  @PostMapping("/v2/order")
  public ResponseEntity<String> makerOderAndSave(
      Principal principal,
      @Valid @RequestBody MakeOrderRequestDTO makeOrderRequestDTO ) {
    return generateSuccessResponse(
        binanceGatewayService.makeAndSaveOrder(principal.getName(), makeOrderRequestDTO));
  }

  @GetMapping("/v1/order")
  public ResponseEntity<String> fetchOrder(
      Principal principal,
      @RequestParam(name = "symbol") String symbol,
      @RequestParam(name = "orderId") Long orderId
  ) {

    AppUser appUser = getAppUser(principal.getName());
    return generateSuccessResponse(
        binanceFeign.checkOrderStatus(new BinanceOrderStatusRequestDTO(symbol, orderId),
            appUser.getApiSecret(), appUser.getApiKey()));
  }

  @GetMapping("/v1/status")
  public ResponseEntity<String> checkStatus(
      Principal principal) {
    BinanceTimestampRequestDTO requestQuery = new BinanceTimestampRequestDTO();
    AppUser appUser = getAppUser(principal.getName());
    return generateSuccessResponse(
        binanceFeign.checkSpotAccountInfo(requestQuery, appUser.getApiSecret(),
            appUser.getApiKey()));
  }

  @GetMapping("/v1/history")
  public ResponseEntity<String> fetchHistory(
      Principal principal,
      @RequestParam(required = false, name = "symbol") String symbol) {
    Long timestamp = new Timestamp(System.currentTimeMillis()).getTime();
    BinanceTradeHistoryRequestDTO requestQuery = new BinanceTradeHistoryRequestDTO(symbol,
        timestamp);
    AppUser appUser = getAppUser(principal.getName());
    return generateSuccessResponse(
        binanceFeign.getTradeHistory(requestQuery, appUser.getApiSecret(),
            appUser.getApiKey()));
  }


  @PostMapping("/v1/migration")
  public ResponseEntity<String> migrateAllHistory(
      Principal principal,
      @RequestParam(name = "symbol") String symbol) {
    return generateSuccessResponse(
        binanceGatewayService.migrateAllTradeHistory(principal.getName(), symbol));
  }

  @PostMapping("/v2/migration")
  public ResponseEntity<String> migrateHistory(
      Principal principal,
      @RequestParam(name = "symbol") String symbol) {
    return generateSuccessResponse(
        binanceGatewayService.migrateTradeHistory(principal.getName(), symbol));
  }


  @GetMapping("/v1/profit")
  public ResponseEntity<String> getProfit(
      Principal principal,
      @RequestParam(name = "crypto_name") String cryptoName) {
    return generateSuccessResponse(
        binanceGatewayService.getProfit(principal.getName(), cryptoName)
    );
  }

  private AppUser getAppUser(String username) {
    return appUserRepository.findByUsername(username).orElseThrow(() ->
        new UsernameNotFoundException(String.format("user %s not found", username)));
  }
}
