package ru.abelogur.tininvestrobot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.core.InvestApi;

@Service
public class SdkService {

    private final String APP_NAME_HEADER = "abelogur";

    private InvestApi investApi;

    @Value("${app.config.token}")
    private String token;

    public InvestApi getInvestApi() {
        if (token == null || token.isBlank()){
            throw new IllegalArgumentException("невалидный токен. Проверьте правильность токена в src/main/resources/application.yaml");
        }
        if (investApi == null) {
            investApi = InvestApi.create(token, APP_NAME_HEADER);
        }
        return investApi;
    }

    public InvestApi getSandboxInvestApi() {
        if (token == null || token.isBlank()){
            throw new IllegalArgumentException("невалидный токен. Проверьте правильность токена в src/main/resources/application.yaml");
        }
        if (investApi == null) {
            investApi = InvestApi.createSandbox(token);
        }
        return investApi;
    }
}
