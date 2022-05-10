package ru.abelogur.tininvestrobot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.core.InvestApi;

@Service
public class SdkService {

    private InvestApi investApi;

    @Value("${app.config.token}")
    private String token;

    public InvestApi getInvestApi() {
        if (token == null || token.isBlank()){
            throw new IllegalArgumentException("невалидный токен. Проверьте правильность токена в src/main/resources/application.yaml");
        }
        if (investApi == null) {
            investApi = InvestApi.create(token);
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
