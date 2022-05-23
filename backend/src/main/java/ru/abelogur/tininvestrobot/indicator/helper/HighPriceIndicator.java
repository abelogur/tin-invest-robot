package ru.abelogur.tininvestrobot.indicator.helper;

import ru.abelogur.tininvestrobot.domain.CachedCandle;

import java.util.List;

/**
 * Индикатор максимального значения в свече
 */
public class HighPriceIndicator extends PriceIndicator {

    public HighPriceIndicator(List<CachedCandle> candles) {
        super(candles, CachedCandle::getHighPrice);
    }
}
