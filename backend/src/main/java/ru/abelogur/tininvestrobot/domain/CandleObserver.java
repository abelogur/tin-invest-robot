package ru.abelogur.tininvestrobot.domain;

public interface CandleObserver {
    void notifyCandle(CandleGroupId groupId, CachedCandle candle);
}
