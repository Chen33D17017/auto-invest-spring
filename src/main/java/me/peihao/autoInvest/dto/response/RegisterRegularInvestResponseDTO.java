package me.peihao.autoInvest.dto.response;

import lombok.Builder;
import lombok.Data;
import me.peihao.autoInvest.dto.RegularInvestDTO;

@Builder
@Data
public class RegisterRegularInvestResponseDTO {

  RegularInvestDTO[] regularInvests;
}
