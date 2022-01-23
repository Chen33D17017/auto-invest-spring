package me.peihao.autoInvest.feign;

import me.peihao.autoInvest.dto.feign.DiscordWebhookDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name="discordFeign",
    url="${discordWebhook.url}"
    //configuration=
)
public interface DiscordFeign {
  @PostMapping("/{webhookId}")
  DiscordWebhookDTO sendWebhook(@PathVariable String webhookId, @RequestBody DiscordWebhookDTO request);
}
