package me.peihao.autoInvest.dto.feign;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DiscordWebhookDTO {
  private String content;
}
