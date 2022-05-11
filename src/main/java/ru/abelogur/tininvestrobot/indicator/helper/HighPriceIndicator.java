package ru.abelogur.tininvestrobot.indicator.helper;

import ru.abelogur.tininvestrobot.indicator.IndicatorCandle;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

public class HighPriceIndicator extends PriceIndicator {

    public HighPriceIndicator(List<IndicatorCandle> candles) {
        super(candles, IndicatorCandle::getHighPrice);
    }
}
