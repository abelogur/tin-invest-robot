package ru.abelogur.tininvestrobot.indicator.helper;

import ru.abelogur.tininvestrobot.domain.CachedCandle;
import ru.abelogur.tininvestrobot.indicator.CachedIndicator;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

/**
 * Базовый класс для индикатора цены
 */
public abstract class PriceIndicator extends CachedIndicator<BigDecimal> {

    private final Function<CachedCandle, BigDecimal> priceFunction;

    protected PriceIndicator(List<CachedCandle> candles, Function<CachedCandle, BigDecimal> priceFunction) {
        super(candles);
        this.priceFunction = priceFunction;
    }

    @Override
    protected BigDecimal calculate(int index) {
        return priceFunction.apply(getCandles().get(index));
    }
}
