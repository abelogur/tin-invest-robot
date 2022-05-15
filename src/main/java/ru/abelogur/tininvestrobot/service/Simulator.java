package ru.abelogur.tininvestrobot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.domain.CachedCandle;
import ru.abelogur.tininvestrobot.domain.CandleGroupId;
import ru.abelogur.tininvestrobot.util.HelperUtils;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Simulator {

    private final SdkService sdkService;
    private final CandleService candleService;

    public void simulate(CandleGroupId groupId, Instant start) {
        var requestPeriod = HelperUtils.getMaxRequestPeriod(groupId.getCandleInterval());
        var from = start;
        var to = from.plus(requestPeriod);
        while (from.isBefore(Instant.now())) {
            List<HistoricCandle> candles = sdkService.getInvestApi().getMarketDataService()
                    .getCandlesSync(groupId.getFigi(), from, to, groupId.getCandleInterval());
            candles.forEach(candle ->
                    candleService.addCandleIfAbsent(groupId, CachedCandle.ofHistoricCandle(candle, BigDecimal.ONE)));
            from = to;
            to = from.plus(requestPeriod);
        }
    }
}