package me.peihao.autoInvest.repository;

import java.util.List;
import java.util.Optional;
import me.peihao.autoInvest.constant.WeekDayEnum;
import me.peihao.autoInvest.model.AppUser;
import me.peihao.autoInvest.model.RegularInvest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RegularInvestRepository extends JpaRepository<RegularInvest, Long> {
  @Query("select ri from RegularInvest ri where ri.appUser.username = ?1")
  List<RegularInvest> findRegularInvestsByAppUserName(String username);

  String SQL_DELETE_FROM_USER_CRYPTO_NAME =
      "delete ri \n"
          + "from regular_invest ri \n"
          + "join app_user au on ri.user_id=au.id \n"
          + "where au.username= ?1 and \n"
          + "ri.crypto_name= ?2 \n";

  @Modifying
  @Query(value = SQL_DELETE_FROM_USER_CRYPTO_NAME, nativeQuery = true)
  void deleteByUsernameAndCryptoName(String username, String cryptoName);

  String SQL_DELETE_FROM_USER_CRYPTO_NAME_WEEKDAY =
      "delete ri \n"
          + "from regular_invest ri \n"
          + "join app_user au on ri.user_id=au.id \n"
          + "where au.username= ?1 and \n"
          + "ri.crypto_name= ?2 and \n"
          + "ri.weekday= ?3 \n";

  @Modifying
  @Query(value = SQL_DELETE_FROM_USER_CRYPTO_NAME_WEEKDAY, nativeQuery = true)
  void deleteByCryptoNameAndUsernameAndWeekday(String username, String cryptoName, String weekday);

  RegularInvest findRegularInvestsByAppUserAndCryptoNameAndWeekday(AppUser appUser,
      String cryptoName, WeekDayEnum weekday);

  List<RegularInvest> findRegularInvestsByWeekday(WeekDayEnum weekday);
}
