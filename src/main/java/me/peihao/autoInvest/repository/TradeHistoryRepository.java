package me.peihao.autoInvest.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import me.peihao.autoInvest.model.TradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TradeHistoryRepository extends JpaRepository<TradeHistory, Long> {
  @Query("select th from TradeHistory th where th.appUser.username = ?1 and th.symbol = ?2")
  List<TradeHistory> findRegularInvestsByUsernameAndSymbol(String username, String symbol);

  @Query("select max(th.time) from TradeHistory th where th.appUser.username = ?1 and th.symbol= ?2")
  LocalDateTime findLastUpdatedHistoryTime(String username, String symbol);
}
