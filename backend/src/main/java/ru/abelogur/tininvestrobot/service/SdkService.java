package ru.abelogur.tininvestrobot.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.core.InvestApi;

@Slf4j
@Service
public class SdkService {

    private final String APP_NAME_HEADER = "abelogur";

    @Getter
    private final InvestApi investApi;
    @Getter
    private final InvestApi sandboxInvestApi;

    public SdkService(@Value("${app.config.token}") String token) {
        if (token == null || token.isBlank()){
            throw new IllegalArgumentException("Невалидный токен. Проверьте правильность токена в src/main/resources/application.yaml");
        }
        investApi = InvestApi.create(token, APP_NAME_HEADER);
        sandboxInvestApi = InvestApi.createSandbox(token, APP_NAME_HEADER);
    }
}
