package me.peihao.autoInvest.dto.requests;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangePasswordDTO {

  @NotNull
  String oldPassword;

  @NotNull
  String newPassword;

}
