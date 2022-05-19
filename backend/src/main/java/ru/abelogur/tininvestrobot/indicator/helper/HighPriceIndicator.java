package ru.abelogur.tininvestrobot.indicator.helper;

import ru.abelogur.tininvestrobot.domain.CachedCandle;

import java.util.List;

public class HighPriceIndicator extends PriceIndicator {

    public HighPriceIndicator(List<CachedCandle> candles) {
        super(candles, CachedCandle::getHighPrice);
    }
}
