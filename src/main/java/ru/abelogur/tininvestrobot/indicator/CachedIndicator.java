package ru.abelogur.tininvestrobot.indicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CachedIndicator<T> extends AbstractIndicator<T> {

    private final Map<Integer, T> cache;

    protected CachedIndicator(Indicator<T> indicator) {
        this(indicator.getCandles());
    }

    protected CachedIndicator(List<IndicatorCandle> candles) {
        super(candles);
        this.cache = new HashMap<>(candles.size());
    }

    public T getValue(int index) {
        if (!cache.containsKey(index)) {
            cache.put(index, calculate(index));
        }
        return cache.get(index);
    }

    protected abstract T calculate(int index);
}
