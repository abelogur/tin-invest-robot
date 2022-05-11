package ru.abelogur.tininvestrobot.indicator.helper;

import ru.abelogur.tininvestrobot.indicator.IndicatorCandle;

import java.util.List;

public class ClosePriceIndicator extends PriceIndicator {

    public ClosePriceIndicator(List<IndicatorCandle> candles) {
        super(candles, IndicatorCandle::getClosePrice);
    }
}
