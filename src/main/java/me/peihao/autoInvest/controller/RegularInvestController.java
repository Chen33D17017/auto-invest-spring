package me.peihao.autoInvest.controller;

import java.io.IOException;
import java.security.Principal;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import me.peihao.autoInvest.dto.requests.PutRegularInvestRequestDTO;
import me.peihao.autoInvest.service.RegularInvestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import me.peihao.autoInvest.dto.requests.RegisterRegularInvestRequestDTO;
import static me.peihao.autoInvest.common.ResultUtil.generateSuccessResponse;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class RegularInvestController {

    private final RegularInvestService regularInvestService;

    @PostMapping("/v1/regular_invest")
    public ResponseEntity<String> registerRegularInvest(
        Principal principal,
        @Valid @RequestBody RegisterRegularInvestRequestDTO registerRegularInvestRequestDTO) {
        return generateSuccessResponse(regularInvestService
            .registerRegularInvest(principal.getName(), registerRegularInvestRequestDTO));
    }

    @GetMapping("/v1/regular_invest")
    public ResponseEntity<String> fetchRegularInvest(Principal principal,
        @RequestParam(required = false, name = "cryptoName") String cryptoName,
        @RequestParam(required = false, name = "weekday") String weekday) {
        return generateSuccessResponse(
            regularInvestService.fetchRegularInvest(principal.getName(), cryptoName, weekday));
    }

    @PutMapping("/v1/regular_invest")
    public ResponseEntity<String> updateRegularInvest(
        Principal principal,
        @Valid @RequestBody PutRegularInvestRequestDTO updateRegularInvestDTO) {
        return generateSuccessResponse(
            regularInvestService.updateRegularInvest(principal.getName(), updateRegularInvestDTO)
        );
    }


    @DeleteMapping("/v1/regular_invest")
    public ResponseEntity<String> deleteRegularInvest(
        Principal principal,
        @RequestParam(name = "cryptoName") String cryptoName,
        @RequestParam(required = false, name="weekday") String weekday
    ){
        return generateSuccessResponse(
            regularInvestService.deleteRegularInvest(principal.getName(), cryptoName, weekday)
        );
    }

    @GetMapping("/v1/fear")
    public ResponseEntity<String> getFearIndex() throws IOException {
        return generateSuccessResponse(
            regularInvestService.getFearIndex()
        );
    }
}
