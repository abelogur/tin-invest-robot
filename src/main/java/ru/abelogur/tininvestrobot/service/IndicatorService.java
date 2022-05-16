package ru.abelogur.tininvestrobot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.domain.CachedCandle;
import ru.abelogur.tininvestrobot.dto.ChartPoint;
import ru.abelogur.tininvestrobot.helper.HelperUtils;
import ru.abelogur.tininvestrobot.indicator.EMAIndicator;
import ru.abelogur.tininvestrobot.indicator.SMAIndicator;
import ru.abelogur.tininvestrobot.indicator.StochasticOscillator;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class IndicatorService {

    private final SdkService sdkService;

    private final HashMap<String, List<CachedCandle>> cache = new HashMap<>();

    public List<ChartPoint> getEmaIndicator(String figi, Integer count, Duration interval) {
        var candleInterval = HelperUtils.intervalFrom(interval);

        var cacheKey = figi+candleInterval;
        final List<CachedCandle> candles;
        if (cache.containsKey(cacheKey)) {
            candles = cache.get(cacheKey);
        } else {
            candles = getDataSet(figi, 1, candleInterval);
            cache.put(cacheKey, candles);
        }
        var indicator = new EMAIndicator(candles, count);
        return IntStream.range(candles.size() - 10, candles.size()).boxed()
                .map(i -> new ChartPoint()
                        .setValue(indicator.getValue(i))
                        .setTime(candles.get(i).getTime()))
                .collect(Collectors.toList());
    }

    public List<ChartPoint> getStochasticOscillatorIndicator(String figi, Integer count,
                                                             Duration interval, Integer smoothing) {
        var candleInterval = HelperUtils.intervalFrom(interval);

        var cacheKey = figi+interval;
        final List<CachedCandle> candles;
        if (cache.containsKey(cacheKey)) {
            candles = cache.get(cacheKey);
        } else {
            candles = getDataSet(figi, 1, candleInterval);
            cache.put(cacheKey, candles);
        }

        var indicator = new SMAIndicator(new StochasticOscillator(candles, count), smoothing);
        return IntStream.range(candles.size() - 10, candles.size()).boxed()
                .map(i -> new ChartPoint()
                        .setValue(indicator.getValue(i))
                        .setTime(candles.get(i).getTime()))
                .collect(Collectors.toList());
    }

    private List<CachedCandle> getDataSet(String figi, int offsetYears, CandleInterval candleInterval) {
        if (offsetYears > 1) {
            return new ArrayList<>();
        }
//        var from = OffsetDateTime.now().minusYears(offsetYears).toInstant();
//        var to = OffsetDateTime.now().minusYears(offsetYears - 1).toInstant();
        var from = OffsetDateTime.now().minusDays(1).toInstant();
        var to = OffsetDateTime.now().toInstant();
        var candles = sdkService.getInvestApi()
                .getMarketDataService().getCandlesSync(figi, from, to, candleInterval).stream()
                .map(it -> CachedCandle.ofHistoricCandle(it, BigDecimal.ONE))
                .collect(TreeSet<CachedCandle>::new, TreeSet::add, TreeSet::addAll);
        if (!candles.isEmpty()) {
            candles.addAll(getDataSet(figi, offsetYears + 1, candleInterval));
        }
        return new ArrayList<>(candles);
    }
}
