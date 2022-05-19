package ru.abelogur.tininvestrobot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.tinkoff.piapi.contract.v1.Account;

@Getter
@AllArgsConstructor
public class AccountDto {
    private String accountId;
    private String name;

    public static AccountDto of(Account account) {
        return new AccountDto(account.getId(), account.getName());
    }
}
