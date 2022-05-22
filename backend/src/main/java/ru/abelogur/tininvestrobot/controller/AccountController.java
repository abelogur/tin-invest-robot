package ru.abelogur.tininvestrobot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.abelogur.tininvestrobot.dto.AccountDto;
import ru.abelogur.tininvestrobot.service.SdkService;
import ru.tinkoff.piapi.contract.v1.AccessLevel;
import ru.tinkoff.piapi.contract.v1.AccountStatus;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Аккаунт")
@RestController
@RequestMapping("account")
@RequiredArgsConstructor
public class AccountController {

    private final SdkService sdkService;

    @Operation(summary = "Все доступные аккаунты")
    @GetMapping
    public List<AccountDto> getAccounts() {
        return sdkService.getInvestApi().getUserService().getAccountsSync().stream()
                .filter(account -> account.getAccessLevel().equals(AccessLevel.ACCOUNT_ACCESS_LEVEL_FULL_ACCESS)
                        && account.getStatus().equals(AccountStatus.ACCOUNT_STATUS_OPEN))
                .map(AccountDto::of)
                .collect(Collectors.toList());
    }
}
