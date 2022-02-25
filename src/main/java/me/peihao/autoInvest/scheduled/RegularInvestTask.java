package me.peihao.autoInvest.scheduled;

import feign.FeignException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.peihao.autoInvest.constant.WeekDayEnum;
import me.peihao.autoInvest.dto.feign.requeset.DiscordMessageRequestDTO;
import me.peihao.autoInvest.dto.feign.response.BinanceOrderResponseDTO;
import me.peihao.autoInvest.dto.requests.MakeOrderRequestDTO;
import me.peihao.autoInvest.dto.response.GetProfitResponseDTO;
import me.peihao.autoInvest.exception.AutoInvestException;
import me.peihao.autoInvest.exception.BinanceFeignException;
import me.peihao.autoInvest.feign.DiscordFeign;
import me.peihao.autoInvest.model.RegularInvest;
import me.peihao.autoInvest.repository.RegularInvestRepository;
import me.peihao.autoInvest.service.BinanceGatewayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableAsync
@EnableScheduling
@Slf4j
public class RegularInvestTask {

  private final RegularInvestRepository regularInvestRepository;
  private final BinanceGatewayService binanceGatewayService;
  private final DiscordFeign discordFeign;
  @Value("${spring.profiles.active}") String env;
  @Value("${webhook.target-id}") String webhookId;
  @Value("${webhook.error-id}") String errorWebhookId;
  @Value("${webhook.wait-time}")

  private final String messageTemplate = "Regular Invest Report\n" +
      "```" +
      "Name                 : %s\n" +
      "Crypto Name          : %s\n" +
      "Price Now            : %f\n" +
      "Buying Amount        : %f\n" +
      "Total Amount         : %f\n" +
      "Average brought Price: %f\n" +
      "Profit Rate          : %s\n" +
      "```" ;

  @Scheduled(cron = "${auto-invest.order.cron}", zone = "${auto-invest.cron.time-zone}")
  void executeOrder() {
    List<RegularInvest> executedRegularInvest = regularInvestRepository
        .findRegularInvestsByWeekday(getWeekDayToday());
    for (RegularInvest regularInvest : executedRegularInvest) {
      try {
        makeOrder(regularInvest);
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        log.error("Fail to wait for next regular invest");
      } catch (AutoInvestException e) {
        discordFeign.sendWebhook(errorWebhookId, new DiscordMessageRequestDTO(String
            .format("%s fail to execute the regular invest: %s",
                regularInvest.getAppUser().getName(), e.getMessage())));
        log.error("{}", String.valueOf(e));
      }
    }
  }

  private void makeOrder(RegularInvest regularInvest){
    log.info("{} buy symbol {} for {}", regularInvest.getAppUser().getUsername(),
        regularInvest.getCryptoName(), regularInvest.getAmount());
      MakeOrderRequestDTO makerOrderRequest = MakeOrderRequestDTO.builder()
          .symbol(regularInvest.getCryptoName())
          .buy_from(regularInvest.getBuyFrom())
          .side("BUY")
          .amount(regularInvest.getAmount())
          .build();
    BinanceOrderResponseDTO buyingResult = binanceGatewayService.makeAndSaveOrder(regularInvest.getAppUser().getUsername(), makerOrderRequest);
    GetProfitResponseDTO profit = binanceGatewayService.getProfit(regularInvest.getAppUser().getUsername(), regularInvest.getCryptoName());
    discordFeign.sendWebhook(webhookId, new DiscordMessageRequestDTO(String.format(
        messageTemplate,
        regularInvest.getAppUser().getName(),
        profit.getCryptoName(),
        profit.getPriceNow(),
        buyingResult.getExecutedQty(),
        profit.getAmount(),
        profit.getAveragePrice(),
        profit.getProfitRate()
    )));
    // Avoid too high tps to webhook
    try {
      TimeUnit.MICROSECONDS.sleep(300);
    } catch (InterruptedException e) {
      log.error("Fail to waiting");
    }
  }

  private WeekDayEnum getWeekDayToday(){
    return WeekDayEnum.valueOf(LocalDate.now(
        Clock.system(ZoneId.of("Asia/Tokyo"))).getDayOfWeek().toString().substring(0, 3));
  }
}
