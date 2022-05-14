package ru.abelogur.tininvestrobot.indicator;

import lombok.Getter;
import ru.abelogur.tininvestrobot.domain.CachedCandle;

import java.util.List;

public abstract class AbstractIndicator<T> implements Indicator<T> {
    @Getter
    private final List<CachedCandle> candles;

    protected AbstractIndicator(List<CachedCandle> candles) {
        this.candles = candles;
    }
}
