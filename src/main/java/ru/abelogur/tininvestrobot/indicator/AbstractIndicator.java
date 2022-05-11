package ru.abelogur.tininvestrobot.indicator;

import lombok.Getter;

import java.util.List;

public abstract class AbstractIndicator<T> implements Indicator<T> {
    @Getter
    private final List<IndicatorCandle> candles;

    protected AbstractIndicator(List<IndicatorCandle> candles) {
        this.candles = candles;
    }
}
