package ru.abelogur.tininvestrobot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.controller.dto.ChartPoint;
import ru.abelogur.tininvestrobot.indicator.EMAIndicator;
import ru.abelogur.tininvestrobot.indicator.IndicatorCandle;
import ru.abelogur.tininvestrobot.indicator.SMAIndicator;
import ru.abelogur.tininvestrobot.indicator.StochasticOscillator;
import ru.abelogur.tininvestrobot.util.HelperUtils;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.core.utils.MapperUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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

    private final HashMap<String, List<IndicatorCandle>> cache = new HashMap<>();

    public List<ChartPoint> getEmaIndicator(String figi, Integer count, Duration interval) {
        var candleInterval = HelperUtils.intervalFrom(interval);

        var cacheKey = figi+candleInterval;
        final List<IndicatorCandle> candles;
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
        final List<IndicatorCandle> candles;
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

    private List<IndicatorCandle> getDataSet(String figi, int offsetYears, CandleInterval candleInterval) {
        if (offsetYears > 1) {
            return new ArrayList<>();
        }
        var from = OffsetDateTime.now().minusYears(offsetYears).toInstant();
        var to = OffsetDateTime.now().minusYears(offsetYears - 1).toInstant();
//        var from = OffsetDateTime.now().minusHours(1).toInstant();
//        var to = OffsetDateTime.now().toInstant();
        var candles = sdkService.getInvestApi()
                .getMarketDataService().getCandlesSync(figi, from, to, candleInterval).stream()
                .map(it -> new IndicatorCandle()
                        .setClosePrice(MapperUtils.quotationToBigDecimal(it.getClose()))
                        .setHighPrice(MapperUtils.quotationToBigDecimal(it.getHigh()))
                        .setLowPrice(MapperUtils.quotationToBigDecimal(it.getLow()))
                        .setTime(OffsetDateTime.ofInstant(Instant.ofEpochSecond(it.getTime().getSeconds()), ZoneId.of("UTC"))))
                .collect(TreeSet<IndicatorCandle>::new, TreeSet::add, TreeSet::addAll);
        if (!candles.isEmpty()) {
            candles.addAll(getDataSet(figi, offsetYears + 1, candleInterval));
        }
        return new ArrayList<>(candles);
    }
}
