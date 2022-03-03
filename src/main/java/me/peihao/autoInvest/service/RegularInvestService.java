package me.peihao.autoInvest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.peihao.autoInvest.constant.ResultInfoConstants;
import me.peihao.autoInvest.constant.WeekDayEnum;
import me.peihao.autoInvest.constant.WhenDuplicateEnum;
import me.peihao.autoInvest.dto.RegularInvestDTO;
import me.peihao.autoInvest.dto.requests.PutRegularInvestRequestDTO;
import me.peihao.autoInvest.dto.requests.RegisterRegularInvestRequestDTO;
import me.peihao.autoInvest.dto.response.FetchRegularInvestResponseDTO;
import me.peihao.autoInvest.dto.response.RegisterRegularInvestResponseDTO;
import me.peihao.autoInvest.exception.AutoInvestException;
import me.peihao.autoInvest.model.AppUser;
import me.peihao.autoInvest.model.RegularInvest;
import me.peihao.autoInvest.repository.AppUserRepository;
import me.peihao.autoInvest.repository.FearIndexRepository;
import me.peihao.autoInvest.repository.RegularInvestRepository;
import me.peihao.autoInvest.validator.WhenDuplicate;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
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

    for (RegularInvestDTO regularInvestDTO : request.getRegularInvests()) {
      RegularInvest existedRegularInvest = regularInvestRepository
          .findRegularInvestsByAppUserAndCryptoNameAndWeekday(targetUser,
              regularInvestDTO.getCryptoName(), WeekDayEnum.valueOf(regularInvestDTO.getWeekday()));

      if (existedRegularInvest != null) {
        switch (WhenDuplicateEnum.forValue(request.getWhenDuplicate())) {
          case error:
            throw new AutoInvestException(ResultInfoConstants.FAIL_ON_REWRITE);
          case ignore:
            continue;
          case overwrite:
            existedRegularInvest.setAmount(regularInvestDTO.getAmount());
            existedRegularInvest.setBuyFrom(regularInvestDTO.getBuyFrom());
            regularInvestRepository.save(existedRegularInvest);
        }
      } else {
        RegularInvest regularInvest = new RegularInvest(targetUser,
            WeekDayEnum.valueOf(regularInvestDTO.getWeekday()), regularInvestDTO.getBuyFrom(),
            regularInvestDTO.getCryptoName(),
            regularInvestDTO.getAmount());
        regularInvestRepository.save(regularInvest);
      }
    }
    return RegisterRegularInvestResponseDTO.builder()
        .regularInvests(request.getRegularInvests()).build();
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

    List<RegularInvestDTO> resultList = new ArrayList<>();
    regularInvestSteam.forEach(a -> resultList.add(new RegularInvestDTO(
        a.getWeekday().name(), a.getBuyFrom(), a.getCryptoName(), a.getAmount()
    )));
    return new FetchRegularInvestResponseDTO(resultList);
  }

  @Transactional
  public FetchRegularInvestResponseDTO updateRegularInvest(String username, PutRegularInvestRequestDTO request){
    AppUser targetUser = appUserRepository.findByUsername(username).orElseThrow(
        () -> new IllegalStateException("Illegal User")
    );
    regularInvestRepository.deleteByUsernameAndCryptoName(username, request.getCryptoName());
    for (String weekdays : request.getWeekdays()){
      RegularInvest regularInvest = new RegularInvest(targetUser,
          WeekDayEnum.valueOf(weekdays), request.getBuyFrom(), request.getCryptoName(),
          request.getAmount());
      regularInvestRepository.save(regularInvest);
    }

    return fetchRegularInvest(username, request.getCryptoName(), null);
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
