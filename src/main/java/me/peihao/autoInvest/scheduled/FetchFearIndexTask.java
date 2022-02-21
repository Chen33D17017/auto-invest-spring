package me.peihao.autoInvest.scheduled;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.peihao.autoInvest.repository.FearIndexRepository;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableAsync
@EnableScheduling
@Slf4j
public class FetchFearIndexTask {

  private final FearIndexRepository fearIndexRepository;


  @Scheduled(cron = "${auto-invest.fear-index.cron}", zone = "${auto-invest.cron.time-zone}")
  void UpdateFearIndex() {
    try{
      fearIndexRepository.UpdateFearIndex();
    } catch (IOException e){
      log.error("Fail to update fear index");
    }
  }
}
