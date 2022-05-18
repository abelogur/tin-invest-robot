package ru.abelogur.tininvestrobot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.core.InvestApi;

@Service
public class SdkService {

    private final String APP_NAME_HEADER = "abelogur";

    private InvestApi investApi;
    private InvestApi sandboxInvestApi;

    @Value("${app.config.token}")
    private String token;

    public synchronized InvestApi getInvestApi() {
        if (token == null || token.isBlank()){
            throw new IllegalArgumentException("Невалидный токен. Проверьте правильность токена в src/main/resources/application.yaml " +
                    "или в переменной окружения INVEST_API_TOKEN");
        }
        if (investApi == null) {
            investApi = InvestApi.create(token, APP_NAME_HEADER);
        }
        return investApi;
    }

    public synchronized InvestApi getInvestSandboxApi() {
        if (token == null || token.isBlank()){
            throw new IllegalArgumentException("Невалидный токен. Проверьте правильность токена в src/main/resources/application.yaml " +
                    "или в переменной окружения INVEST_API_TOKEN");
        }
        if (sandboxInvestApi == null) {
            sandboxInvestApi = InvestApi.createSandbox(token, APP_NAME_HEADER);
        }
        return sandboxInvestApi;
    }
}
