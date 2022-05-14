package ru.abelogur.tininvestrobot.indicator.helper;

import ru.abelogur.tininvestrobot.domain.CachedCandle;

import java.util.List;

public class LowPriceIndicator extends PriceIndicator {

    public LowPriceIndicator(List<CachedCandle> candles) {
        super(candles, CachedCandle::getLowPrice);
    }
}
