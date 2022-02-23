package me.peihao.autoInvest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.peihao.autoInvest.model.AppUser;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUserResponseDTO {
  private String name;
  private String username;
  private String email;

  public static GetUserResponseDTO generateUserResponseDTO(AppUser appUser){
    return new GetUserResponseDTO(appUser.getName(), appUser.getUsername(), appUser.getEmail());
  }

}
