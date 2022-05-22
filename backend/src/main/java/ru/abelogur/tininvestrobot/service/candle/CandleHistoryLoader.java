package ru.abelogur.tininvestrobot.service.candle;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.abelogur.tininvestrobot.domain.CandleGroupId;
import ru.abelogur.tininvestrobot.dto.LoadPeriod;
import ru.abelogur.tininvestrobot.helper.HelperUtils;
import ru.abelogur.tininvestrobot.service.SdkService;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CandleHistoryLoader {

    private final SdkService sdkService;

    public List<HistoricCandle> load(CandleGroupId groupId) {
        var finish = Instant.now();
        return load(groupId, getStartDate(groupId.getCandleInterval(), finish), finish);
    }

    public List<HistoricCandle> loadFrom(CandleGroupId groupId, Instant start) {
        return load(groupId, start, Instant.now());
    }

    public List<HistoricCandle> loadTo(CandleGroupId groupId, Instant finish) {
        return load(groupId, getStartDate(groupId.getCandleInterval(), finish), finish);
    }

    public List<HistoricCandle> load(CandleGroupId groupId, Instant start, Instant finish) {
        Duration periodDuration = HelperUtils.getMaxRequestPeriod(groupId.getCandleInterval());
        List<LoadPeriod> periods = new ArrayList<>();
        var from = start;
        var to = from.plus(periodDuration);
        while (from.isBefore(finish)) {
            periods.add(new LoadPeriod(from, to));
            from = to;
            to = from.plus(periodDuration);
        }

        var futures = loadParallel(periods, groupId);
        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Instant getStartDate(CandleInterval interval, Instant finish) {
        switch (interval) {
            case CANDLE_INTERVAL_1_MIN:
                return finish.minus(10, ChronoUnit.DAYS);
            case CANDLE_INTERVAL_5_MIN:
                return finish.minus(30, ChronoUnit.DAYS);
            case CANDLE_INTERVAL_15_MIN:
                return finish.minus(50, ChronoUnit.DAYS);
            case CANDLE_INTERVAL_HOUR:
                return finish.minus(100, ChronoUnit.DAYS);
            case CANDLE_INTERVAL_DAY:
                return finish.minus(365 * 3, ChronoUnit.DAYS);
            default:
                throw new IllegalArgumentException("Invalid candle interval");
        }
    }

    private List<CompletableFuture<List<HistoricCandle>>> loadParallel(List<LoadPeriod> periods, CandleGroupId groupId) {
        var futures = periods.stream()
                .map(period -> sdkService.getInvestApi().getMarketDataService()
                        .getCandles(groupId.getFigi(), period.getFrom(), period.getTo(), groupId.getCandleInterval()))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        return futures;
    }
}
