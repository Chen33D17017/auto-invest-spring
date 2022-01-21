package me.peihao.autoInvest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PatchUserResponseDTO {
  private String name;
  private String userName;
  private String email;
}
