package ru.abelogur.tininvestrobot.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.abelogur.tininvestrobot.domain.CachedCandle;
import ru.abelogur.tininvestrobot.domain.CandleGroupId;
import ru.abelogur.tininvestrobot.repository.InstrumentRepository;
import ru.abelogur.tininvestrobot.service.CandleService;
import ru.abelogur.tininvestrobot.service.SdkService;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.MarketDataResponse;
import ru.tinkoff.piapi.contract.v1.SubscriptionStatus;
import ru.tinkoff.piapi.core.stream.MarketDataSubscriptionService;
import ru.tinkoff.piapi.core.stream.StreamProcessor;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static ru.tinkoff.piapi.contract.v1.SubscriptionInterval.SUBSCRIPTION_INTERVAL_FIVE_MINUTES;
import static ru.tinkoff.piapi.contract.v1.SubscriptionInterval.SUBSCRIPTION_INTERVAL_ONE_MINUTE;

@Slf4j
@Component
public class CandleSteamsHolder {

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(8);
    private final Map<CandleGroupId, ScheduledFuture<?>> refreshers = new HashMap<>();

    private final SdkService sdkService;
    private final CandleService candleService;
    private final InstrumentRepository instrumentRepository;
    private final MarketDataSubscriptionService subscriptionService;

    public CandleSteamsHolder(SdkService sdkService, CandleService candleService,
                              InstrumentRepository instrumentRepository) {
        this.sdkService = sdkService;
        this.candleService = candleService;
        this.instrumentRepository = instrumentRepository;
        this.subscriptionService = getSubscriptionService();
    }

    public void addNewSubscription(CandleGroupId groupId) {
        CandleInterval interval = CandleInterval.valueOf(groupId.getInterval());
        if (interval.equals(CandleInterval.CANDLE_INTERVAL_1_MIN)) {
            subscriptionService.subscribeCandles(List.of(groupId.getFigi()), SUBSCRIPTION_INTERVAL_ONE_MINUTE);
        } else if (interval.equals(CandleInterval.CANDLE_INTERVAL_5_MIN)) {
            subscriptionService.subscribeCandles(List.of(groupId.getFigi()), SUBSCRIPTION_INTERVAL_FIVE_MINUTES);
        } else {
            final long period;
            final TimeUnit unit;
            if (interval.equals(CandleInterval.CANDLE_INTERVAL_15_MIN)) {
                period = 15;
                unit = TimeUnit.MINUTES;
            } else if (interval.equals(CandleInterval.CANDLE_INTERVAL_HOUR)) {
                period = 1;
                unit = TimeUnit.HOURS;
            } else if (interval.equals(CandleInterval.CANDLE_INTERVAL_DAY)) {
                period = 1;
                unit = TimeUnit.DAYS;
            } else {
                return;
            }
            ScheduledFuture<?> refresher = executorService
                    .scheduleAtFixedRate(refreshCandles(groupId, interval), 0, period, unit);
            if (refreshers.containsKey(groupId)) {
                refreshers.get(groupId).cancel(false);
            }
            refreshers.put(groupId, refresher);
        }
    }

    public void removeSubscription(CandleGroupId groupId) {
        if (refreshers.containsKey(groupId)) {
            refreshers.get(groupId).cancel(false);
            refreshers.remove(groupId);
        }
    }

    private MarketDataSubscriptionService getSubscriptionService() {
        StreamProcessor<MarketDataResponse> processor = response -> {
            if (response.hasPing()) {
                log.info("Market data stream ping");
            } else if (response.hasCandle()) {
                saveCandle(response);
            } else if (response.hasSubscribeCandlesResponse()) {
                handleCandleSubscribe(response);
            }
        };

        Consumer<Throwable> onErrorCallback = error -> log.error(error.toString());
        return sdkService.getInvestApi().getMarketDataStreamService()
                .newStream("candles_stream", processor, onErrorCallback);
    }

    private void saveCandle(MarketDataResponse response) {
        var candle = response.getCandle();
        var groupId = CandleGroupId.of(candle.getFigi(), candle.getInterval());
        var cachedCandle = CachedCandle.ofStreamCandle(candle, instrumentRepository.get(groupId.getFigi()).getLot());
        candleService.addCandleIfAbsent(groupId, cachedCandle);
    }

    private void handleCandleSubscribe(MarketDataResponse response) {
        var successCount = response.getSubscribeCandlesResponse().getCandlesSubscriptionsList().stream()
                .filter(el -> el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS)).count();
        var errorCount = response.getSubscribeTradesResponse().getTradeSubscriptionsList().stream()
                .filter(el -> !el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS)).count();
        log.info("удачных подписок на свечи: {}", successCount);
        log.info("неудачных подписок на свечи: {}", errorCount);
    }

    private Runnable refreshCandles(CandleGroupId groupId, CandleInterval interval) {
        return () -> {
            var from = Instant.now().minus(HelperUtils.durationFrom(interval));
            sdkService.getInvestApi().getMarketDataService().getCandles(groupId.getFigi(), from, Instant.now(), interval)
                    .thenAccept(candles -> {
                        var lastCandles = candles.get(candles.size() - 1);
                        var lots = instrumentRepository.get(groupId.getFigi()).getLot();
                        candleService.addCandleIfAbsent(groupId, CachedCandle.ofHistoricCandle(lastCandles, lots));
                    });
        };
    }
}
