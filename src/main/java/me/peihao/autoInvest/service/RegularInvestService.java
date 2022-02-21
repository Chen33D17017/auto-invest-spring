package me.peihao.autoInvest.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.peihao.autoInvest.constant.ResultInfoConstants;
import me.peihao.autoInvest.constant.WeekDayEnum;
import me.peihao.autoInvest.dto.requests.PutRegularInvestRequestDTO;
import me.peihao.autoInvest.dto.requests.RegisterRegularInvestRequestDTO;
import me.peihao.autoInvest.dto.response.FetchRegularInvestResponseDTO;
import me.peihao.autoInvest.dto.response.PutRegularInvestResponseDTO;
import me.peihao.autoInvest.dto.response.RegisterRegularInvestResponseDTO;
import me.peihao.autoInvest.exception.AutoInvestException;
import me.peihao.autoInvest.model.AppUser;
import me.peihao.autoInvest.model.RegularInvest;
import me.peihao.autoInvest.repository.AppUserRepository;
import me.peihao.autoInvest.repository.FearIndexRepository;
import me.peihao.autoInvest.repository.RegularInvestRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class RegularInvestService {

  private final RegularInvestRepository regularInvestRepository;
  private final AppUserRepository appUserRepository;
  private final FearIndexRepository fearIndexRepository;

  @Transactional
  public RegisterRegularInvestResponseDTO registerRegularInvest(String username,
      RegisterRegularInvestRequestDTO request) {
    AppUser targetUser = appUserRepository.findByUsername(username).orElseThrow(
        () -> new IllegalStateException("Illegal User")
    );
    for (String weekdays : request.getWeekdays()){
      RegularInvest regularInvest = new RegularInvest(targetUser,
          WeekDayEnum.valueOf(weekdays), request.getBuyFrom(), request.getCryptoName(),
          request.getAmount());
      regularInvestRepository.save(regularInvest);
    }
    return RegisterRegularInvestResponseDTO.builder()
        .weekdays(request.getWeekdays()).buyFrom(request.getBuyFrom())
        .cryptoName(request.getCryptoName()).amount(request.getAmount()).build();
  }

  public FetchRegularInvestResponseDTO fetchRegularInvest(String username, String cryptoName, String weekday){
    // TODO: check what happened when don't have result
    Stream<RegularInvest> regularInvestSteam = regularInvestRepository.findRegularInvestsByAppUserName(username).stream();
    if(cryptoName != null){
      regularInvestSteam = regularInvestSteam.filter(ri -> ri.getCryptoName()
          .equals(cryptoName));
    }

    if(weekday != null){
      regularInvestSteam = regularInvestSteam.filter(ri -> ri.getWeekday().name()
          .equals(weekday));
    }

    List<FetchRegularInvestResponseDTO.RegularInvest> resultList = new ArrayList<>();
    regularInvestSteam.forEach(a -> resultList.add(new FetchRegularInvestResponseDTO.RegularInvest(
        a.getWeekday(), a.getBuyFrom(), a.getCryptoName(), a.getAmount()
    )));
    return new FetchRegularInvestResponseDTO(resultList);
  }

  @Transactional
  public FetchRegularInvestResponseDTO updateRegularInvest(String username, String cryptoName, PutRegularInvestRequestDTO request){
    AppUser targetUser = appUserRepository.findByUsername(username).orElseThrow(
        () -> new IllegalStateException("Illegal User")
    );
    regularInvestRepository.deleteByUsernameAndCryptoName(username, cryptoName);
    for (String weekdays : request.getWeekdays()){
      RegularInvest regularInvest = new RegularInvest(targetUser,
          WeekDayEnum.valueOf(weekdays), request.getBuyFrom(), cryptoName,
          request.getAmount());
      regularInvestRepository.save(regularInvest);
    }

    return fetchRegularInvest(username, cryptoName, null);
  }

  @Transactional
  public FetchRegularInvestResponseDTO deleteRegularInvest(String username, String cryptoName,
      String weekday) {
    if (weekday == null) {
      regularInvestRepository.deleteByUsernameAndCryptoName(username, cryptoName);
    } else {
      WeekDayEnum.valueOf(weekday);
      regularInvestRepository.deleteByCryptoNameAndUsernameAndWeekday(username, cryptoName,
          weekday);
    }
    return fetchRegularInvest(username, cryptoName, null);
  }

  public String getFearIndex(){
    return fearIndexRepository.GetFearIndex().orElseThrow(
        () -> new AutoInvestException(ResultInfoConstants.FAIL_GETTING_INDEX)
    );
  }
}
