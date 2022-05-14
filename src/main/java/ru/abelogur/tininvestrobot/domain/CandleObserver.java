package ru.abelogur.tininvestrobot.domain;

public interface CandleObserver {
    void notifyCandle(CachedCandle candle);
}
