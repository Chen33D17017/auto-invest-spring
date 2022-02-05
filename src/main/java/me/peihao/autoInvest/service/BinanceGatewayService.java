package me.peihao.autoInvest.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.peihao.autoInvest.constant.ResultInfoConstants;
import me.peihao.autoInvest.dto.feign.requeset.BinanceTimestampRequestDTO;
import me.peihao.autoInvest.dto.feign.requeset.BinanceFlexibleProductPositionRequestDTO;
import me.peihao.autoInvest.dto.feign.requeset.BinanceOrderRequestDTO;
import me.peihao.autoInvest.dto.feign.requeset.BinanceOrderStatusRequestDTO;
import me.peihao.autoInvest.dto.feign.requeset.BinanceRedeemFlexibleProductRequestDTO;
import me.peihao.autoInvest.dto.feign.requeset.BinanceTradeHistoryRequestDTO;
import me.peihao.autoInvest.dto.feign.response.BinanceAccountResponseDTO;
import me.peihao.autoInvest.dto.feign.response.BinanceAccountResponseDTO.BalanceInfo;
import me.peihao.autoInvest.dto.feign.response.BinanceFlexibleProductPositionResponseDTO;
import me.peihao.autoInvest.dto.feign.response.BinanceOrderResponseDTO;
import me.peihao.autoInvest.dto.feign.response.BinanceTradeHistoryResponseDTO;
import me.peihao.autoInvest.dto.requests.MakeOrderRequestDTO;
import me.peihao.autoInvest.dto.response.GetProfitResponseDTO;
import me.peihao.autoInvest.exception.AutoInvestException;
import me.peihao.autoInvest.feign.BinanceFeign;
import me.peihao.autoInvest.model.AppUser;
import me.peihao.autoInvest.model.TradeHistory;
import me.peihao.autoInvest.repository.AppUserRepository;
import me.peihao.autoInvest.repository.TradeHistoryRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class BinanceGatewayService {

  final private AppUserRepository appUserRepository;
  final private BinanceFeign binanceFeign;
  final private TradeHistoryRepository tradeHistoryRepository;
  final private List<String> stableCoinLists = Arrays.asList("BUSD", "USDT", "USDC", "UST");

  public BinanceOrderResponseDTO makeAndSaveOrder(String username, MakeOrderRequestDTO request) {
    AppUser targetUser = appUserRepository.findByUsername(username).orElseThrow(
        () -> new UsernameNotFoundException("Fail to retrieve user")
    );

    // check balance is enough or not, if not, redeem from flexible product
    Float balance = getSymbolBalance(targetUser, request.getBuy_from());
    if (balance < request.getAmount()) {
      Float needAmount = request.getAmount() - balance + 1;
      log.info("User's balance: {}, try to find remain amount {} from flexible product", balance,
          needAmount);
      BinanceFlexibleProductPositionResponseDTO[] flexibleAssets = binanceFeign
          .getFlexibleProductPosition(new BinanceFlexibleProductPositionRequestDTO(
              request.getBuy_from()), targetUser.getApiSecret(), targetUser.getApiKey());

      BinanceFlexibleProductPositionResponseDTO availableProduct = Arrays.stream(flexibleAssets)
          .filter(f -> f.getFreeAmount() > needAmount).findAny().orElseThrow(
              () -> new AutoInvestException(ResultInfoConstants.BALANCE_INSUFFICIENT));
      binanceFeign.redeemFlexibleProduct(
          new BinanceRedeemFlexibleProductRequestDTO(availableProduct.getProductId(), needAmount),
          targetUser.getApiSecret(),
          targetUser.getApiKey());

      // Just in case, waiting one second for redeem
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        log.error("Fail to waiting");
      }
    }

    BinanceOrderResponseDTO binanceResponse = binanceFeign.newOrder(
        BinanceOrderRequestDTO.builder()
            .symbol(request.getSymbol() + request.getBuy_from())
            .side("BUY")
            .type("MARKET")
            .quoteOrderQty(request.getAmount())
            .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
            .build(), targetUser.getApiSecret(), targetUser.getApiKey()
    );

    saveAfterOrder(targetUser, binanceResponse);
    return binanceResponse;
  }

  public BinanceTradeHistoryResponseDTO[] migrateTradeHistory(String username, String symbol) {
    AppUser targetUser = appUserRepository.findByUsername(username).orElseThrow(
        () -> new UsernameNotFoundException("Fail to retrieve user")
    );

    BinanceTradeHistoryRequestDTO request = new BinanceTradeHistoryRequestDTO(symbol);
    return saveOrders(targetUser, request);
  }

  @Transactional
  public BinanceTradeHistoryResponseDTO[] saveOrders(AppUser targetUser,
      BinanceTradeHistoryRequestDTO request) {
    BinanceTradeHistoryResponseDTO prev = null;
    BinanceTradeHistoryResponseDTO[] histories = binanceFeign
        .getTradeHistory(request, targetUser.getApiSecret(), targetUser.getApiKey());
    for (BinanceTradeHistoryResponseDTO history : histories) {
      if (prev == null) {
        prev = history;
      } else if (prev.getOrderId().equals(history.getOrderId())) {
        prev.setPrice(
            (prev.getPrice() * prev.getQty() + history.getQty() * history.getPrice()) / (
                prev.getQty() + history.getQty()));
        prev.setQty(prev.getQty() + history.getQty());
      } else {
        saveHistory(prev, targetUser);
        prev = history;
      }
    }
    saveHistory(prev, targetUser);

    return histories;
  }

  private void saveHistory(BinanceTradeHistoryResponseDTO history, AppUser targetUser){
    try {
      tradeHistoryRepository.save(TradeHistory.builder()
          .orderId(history.getOrderId())
          .symbol(history.getSymbol())
          .appUser(targetUser)
          .amount(history.getQty())
          .price(history.getPrice())
          .side(history.getIsBuyer() ? "buy" : "sell")
          .time(getLocalDateTime(history.getTime()))
          .build());
    } catch (ConstraintViolationException e) {
      log.warn("Order with orderId : {} is already exists", history.getOrderId());
    }
  }

  public GetProfitResponseDTO getProfit(String username, String cryptoName) {
    List<TradeHistory> histories = new ArrayList<>();
    for (String stableCoin : stableCoinLists) {
      String symbol = cryptoName + stableCoin;
      histories.addAll(tradeHistoryRepository
          .findRegularInvestsByUsernameAndSymbol(username, symbol));
    }

    if (histories.size() == 0) {
      throw new AutoInvestException(ResultInfoConstants.TRADE_HISTORY_NOT_FOUND);
    }
    float totalAmount = 0f;
    float totalCost = 0f;
    for (TradeHistory history : histories) {
      if (history.getSide().equals("buy")) {
        totalAmount += history.getAmount();
        totalCost += history.getAmount() * history.getPrice();
      } else {
        totalAmount -= history.getAmount();
        totalCost -= history.getAmount() * history.getPrice();
      }
    }

    Float averagePrice = totalCost / totalAmount;
    Float priceNow = binanceFeign.getPrice(cryptoName + "USDT").getPrice();
    Float profitRate = (priceNow - averagePrice) / averagePrice * 100;

    return GetProfitResponseDTO.builder().cryptoName(cryptoName)
        .amount(totalAmount).totalCost(totalCost).averagePrice(averagePrice)
        .priceNow(priceNow).profitRate(String.format("%.2f%%", profitRate)).build();
  }

  @Async
  public void saveAfterOrder(AppUser targetUser, BinanceOrderResponseDTO response) {
    if (response.getStatus().equals("FILLED")) {
      BinanceTradeHistoryRequestDTO request = new BinanceTradeHistoryRequestDTO(response.getSymbol(),
          response.getOrderId());
      saveOrders(targetUser, request);
    } else {
      // If the order is not completed, waiting for 1 seconds and get order history again
      try {
        TimeUnit.MICROSECONDS.sleep(300);
      } catch (InterruptedException e) {
        log.error("Fail to waiting");
      }

      BinanceOrderStatusRequestDTO request = new BinanceOrderStatusRequestDTO(response.getSymbol(),
          response.getOrderId());
      BinanceOrderResponseDTO orderStatus = binanceFeign
          .checkOrderStatus(request, targetUser.getApiSecret(), targetUser.getApiKey());
      // try again
      saveAfterOrder(targetUser, orderStatus);
    }
  }

  private LocalDateTime getLocalDateTime(Long timestamp) {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp),
        TimeZone.getDefault().toZoneId());
  }

  private Float getSymbolBalance(AppUser targetUser, String symbol) {
    BinanceAccountResponseDTO response = binanceFeign
        .checkSpotAccountInfo(new BinanceTimestampRequestDTO(), targetUser.getApiSecret(),
            targetUser.getApiKey());
    BalanceInfo balanceInfo = Arrays.stream(response.getBalances())
        .filter(r -> r.getAsset().equals(symbol)).findAny().orElseThrow(
            () -> new AutoInvestException(ResultInfoConstants.FAIL_GETTING_ASSET)
        );
    return balanceInfo.getFree();
  }
}
