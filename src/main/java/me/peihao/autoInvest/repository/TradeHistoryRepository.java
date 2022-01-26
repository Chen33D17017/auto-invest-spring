package me.peihao.autoInvest.repository;

import java.util.List;
import me.peihao.autoInvest.model.TradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TradeHistoryRepository extends JpaRepository<TradeHistory, Long> {
  @Query("select th from TradeHistory th where th.appUser.username = ?1 and th.symbol = ?2")
  List<TradeHistory> findRegularInvestsByUsernameAndSymbol(String username, String symbol);
}
