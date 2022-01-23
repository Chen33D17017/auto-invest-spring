package me.peihao.autoInvest.dto.requests;
import lombok.Data;
import lombok.Value;

@Value
public class PatchUserRequestDTO {
  String name;
  String email;
  String password;
  String apiKey;
  String apiSecret;
}
