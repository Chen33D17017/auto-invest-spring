package me.peihao.autoInvest.dto.requests;
import lombok.Data;

@Data
public class PatchUserRequestDTO {
  private String name;
  private String email;
  private String password;
}
