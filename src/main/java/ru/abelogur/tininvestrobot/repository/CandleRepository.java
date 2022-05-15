package ru.abelogur.tininvestrobot.repository;

import ru.abelogur.tininvestrobot.domain.CachedCandle;
import ru.abelogur.tininvestrobot.domain.CandleGroupId;

import java.util.Collection;
import java.util.SortedSet;

public interface CandleRepository {
    boolean add(CandleGroupId groupId, CachedCandle candle);

    void addAll(CandleGroupId groupId, Collection<CachedCandle> candle);

    SortedSet<CachedCandle> getAll(CandleGroupId groupId);

    void remove(CandleGroupId groupId);
}
