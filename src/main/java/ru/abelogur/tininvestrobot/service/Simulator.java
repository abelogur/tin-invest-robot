package ru.abelogur.tininvestrobot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.domain.InvestBot;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class Simulator {

    private final SdkService sdkService;

    public void simulate(InvestBot bot, String figi,
                         OffsetDateTime start, CandleInterval interval) {
        var from = start.toInstant();
        var to = start.plusDays(1).toInstant();
//        sdkService.getInvestApi().getMarketDataService().getCandlesSync(figi, interval);
    }
}
