package me.peihao.autoInvest.feign;

import me.peihao.autoInvest.dto.feign.requeset.DiscordMessageRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name="discordFeign",
    url="${webhook.endpoint}"
)
public interface DiscordFeign {
  @PostMapping("/{webhookId}")
  DiscordMessageRequestDTO sendWebhook(@PathVariable String webhookId, @RequestBody DiscordMessageRequestDTO request);
}
