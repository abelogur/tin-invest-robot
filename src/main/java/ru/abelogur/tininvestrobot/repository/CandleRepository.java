package ru.abelogur.tininvestrobot.repository;

import ru.abelogur.tininvestrobot.domain.CachedCandle;

import java.util.Collection;

public interface CandleRepository {
    boolean add(String figi, CachedCandle candle);

    void addAll(String figi, Collection<CachedCandle> candle);

    Collection<CachedCandle> getAll(String figi);
}
