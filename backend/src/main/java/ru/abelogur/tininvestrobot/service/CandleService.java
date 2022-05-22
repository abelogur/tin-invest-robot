package ru.abelogur.tininvestrobot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.domain.CachedCandle;
import ru.abelogur.tininvestrobot.domain.CandleGroupId;
import ru.abelogur.tininvestrobot.domain.CandleObserver;
import ru.abelogur.tininvestrobot.helper.CandleHistoryLoader;
import ru.abelogur.tininvestrobot.repository.CandleRepository;
import ru.abelogur.tininvestrobot.repository.InstrumentRepository;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandleService {

    private final Map<CandleGroupId, List<CandleObserver>> candleObservers = new HashMap<>();

    private final CandleRepository candleRepository;
    private final InstrumentRepository instrumentRepository;
    private final CandleHistoryLoader candleHistoryLoader;

    public void addCandleIfAbsent(CandleGroupId groupId, CachedCandle candle) {
        if (candleRepository.add(groupId, candle)) {
            notifyObservers(groupId, candle);
        }
    }

    public void addObserver(CandleGroupId groupId, CandleObserver observer) {
        candleObservers.putIfAbsent(groupId, new ArrayList<>());
        candleObservers.get(groupId).add(observer);
    }

    public void removeObserver(CandleGroupId groupId, CandleObserver observer) {
        if (candleObservers.containsKey(groupId)) {
            candleObservers.get(groupId).remove(observer);
        }
    }

    public SortedSet<CachedCandle> loadHistoricCandles(CandleGroupId groupId) {
        SortedSet<CachedCandle> candles = candleRepository.getAll(groupId);
        List<HistoricCandle> newCandles;
        if (candles.isEmpty()) {
            newCandles = candleHistoryLoader.load(groupId);
        } else {
            Instant lastCandleTime = candles.last().getTime();
            newCandles = candleHistoryLoader.loadFrom(groupId, lastCandleTime);
        }
        var candlesToSave = newCandles.stream()
                .map(it -> CachedCandle.ofHistoricCandle(it, instrumentRepository.get(groupId.getFigi()).getLot()))
                .collect(Collectors.toList());
        candleRepository.addAll(groupId, candlesToSave);
        return candleRepository.getAll(groupId);
    }

    public SortedSet<CachedCandle> loadHistoricCandles(CandleGroupId groupId, Instant finish) {
        SortedSet<CachedCandle> candles = candleRepository.getAll(groupId);
        List<HistoricCandle> newCandles;
        if (candles.isEmpty()) {
            newCandles = candleHistoryLoader.loadTo(groupId, finish);
        } else {
            Instant lastCandleTime = candles.last().getTime();
            newCandles = candleHistoryLoader.load(groupId, lastCandleTime, finish);
        }
        var candlesToSave = newCandles.stream()
                .map(it -> CachedCandle.ofHistoricCandle(it, instrumentRepository.get(groupId.getFigi()).getLot()))
                .collect(Collectors.toList());
        candleRepository.addAll(groupId, candlesToSave);
        return candleRepository.getAll(groupId);
    }

    public void uncached(CandleGroupId groupId) {
        candleRepository.remove(groupId);
    }

    private void notifyObservers(CandleGroupId groupId, CachedCandle candle) {
        if (candleObservers.containsKey(groupId)) {
            candleObservers.get(groupId).forEach(observer -> observer.notifyCandle(groupId, candle));
        }
    }

}
