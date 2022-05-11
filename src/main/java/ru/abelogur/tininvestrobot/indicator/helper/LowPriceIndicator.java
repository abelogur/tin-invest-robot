package ru.abelogur.tininvestrobot.indicator.helper;

import ru.abelogur.tininvestrobot.indicator.IndicatorCandle;

import java.util.List;

public class LowPriceIndicator extends PriceIndicator {

    public LowPriceIndicator(List<IndicatorCandle> candles) {
        super(candles, IndicatorCandle::getLowPrice);
    }
}
