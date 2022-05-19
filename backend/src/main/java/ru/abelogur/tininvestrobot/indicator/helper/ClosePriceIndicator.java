package ru.abelogur.tininvestrobot.indicator.helper;

import ru.abelogur.tininvestrobot.domain.CachedCandle;

import java.util.List;

public class ClosePriceIndicator extends PriceIndicator {

    public ClosePriceIndicator(List<CachedCandle> candles) {
        super(candles, CachedCandle::getClosePrice);
    }
}
