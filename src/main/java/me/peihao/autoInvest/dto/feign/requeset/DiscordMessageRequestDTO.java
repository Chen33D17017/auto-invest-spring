package me.peihao.autoInvest.dto.feign.requeset;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DiscordMessageRequestDTO {
  private String content;
}
