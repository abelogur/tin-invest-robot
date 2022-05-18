package ru.abelogur.tininvestrobot.dto;

import lombok.AllArgsConstructor;
import ru.tinkoff.piapi.contract.v1.Account;

@AllArgsConstructor
public class AccountDto {
    private String accountId;
    private String name;

    public static AccountDto of(Account account) {
        return new AccountDto(account.getId(), account.getName());
    }
}
