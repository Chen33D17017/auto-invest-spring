package me.peihao.autoInvest.repository;

import java.io.IOException;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FearIndexRepository {
  private static final String FEAR_INDEX_KEY = "fear_index";
  private final RedisTemplate<Object, Object> redisTemplate;

  @Autowired
  public FearIndexRepository(
      RedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }


  public void UpdateFearIndex() throws IOException {
    Document doc = Jsoup.connect("https://alternative.me/crypto/fear-and-greed-index/").get();
    Elements elem = doc
        .selectXpath("//*[@id=\"main\"]/section/div/div[3]/div[2]/div/div/div[1]/div[2]/div");

    redisTemplate.opsForValue().set(FEAR_INDEX_KEY, elem.text());
  }

  public Optional<String> GetFearIndex() {
    return Optional.ofNullable((String) redisTemplate.opsForValue()
        .get(FEAR_INDEX_KEY));
  }
}
