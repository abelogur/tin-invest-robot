package ru.abelogur.tininvestrobot.indicator.helper;

import ru.abelogur.tininvestrobot.indicator.CachedIndicator;
import ru.abelogur.tininvestrobot.indicator.Indicator;
import ru.abelogur.tininvestrobot.indicator.IndicatorCandle;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

public abstract class PriceIndicator extends CachedIndicator<BigDecimal> {

    private final Function<IndicatorCandle, BigDecimal> priceFunction;

    protected PriceIndicator(List<IndicatorCandle> candles, Function<IndicatorCandle, BigDecimal> priceFunction) {
        super(candles);
        this.priceFunction = priceFunction;
    }

    @Override
    protected BigDecimal calculate(int index) {
        return priceFunction.apply(getCandles().get(index));
    }
}
