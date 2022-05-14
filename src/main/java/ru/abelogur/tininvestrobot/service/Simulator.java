package ru.abelogur.tininvestrobot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.domain.CachedCandle;
import ru.abelogur.tininvestrobot.domain.CandleGroupId;
import ru.abelogur.tininvestrobot.util.HelperUtils;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Simulator {

    private final SdkService sdkService;
    private final CandleService candleService;

    public void simulate(String figi, Instant start, CandleInterval interval) {
        var requestPeriod = HelperUtils.getMaxRequestPeriod(interval);
        var groupId = CandleGroupId.of(figi, interval);
        var hasNext = true;
        var from = start;
        var to = from.plus(requestPeriod);
        while (hasNext) {
            List<HistoricCandle> candles = sdkService.getInvestApi().getMarketDataService().getCandlesSync(figi, from, to, interval);
            candles.forEach(candle ->
                    candleService.addCandleIfAbsent(groupId, CachedCandle.ofHistoricCandle(candle, BigDecimal.ONE)));
            if (candles.isEmpty()) {
                hasNext = false;
            } else {
                from = to;
                to = from.plus(requestPeriod);
            }
        }
    }
}