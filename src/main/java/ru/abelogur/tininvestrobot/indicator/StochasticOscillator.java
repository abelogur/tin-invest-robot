package ru.abelogur.tininvestrobot.indicator;

import ru.abelogur.tininvestrobot.indicator.helper.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class StochasticOscillator extends CachedIndicator<BigDecimal> {

    private final ClosePriceIndicator indicator;
    private final HighestValueIndicator highestValueIndicator;
    private final LowestValueIndicator lowestValueIndicator;

    public StochasticOscillator(List<IndicatorCandle> candles, int candleCount) {
       this(new ClosePriceIndicator(candles), candleCount);
    }

    public StochasticOscillator(ClosePriceIndicator closePriceIndicator, int candleCount) {
        super(closePriceIndicator);
        this.indicator = closePriceIndicator;
        this.highestValueIndicator = new HighestValueIndicator(
                new HighPriceIndicator(closePriceIndicator.getCandles()), candleCount);
        this.lowestValueIndicator = new LowestValueIndicator(
                new LowPriceIndicator(closePriceIndicator.getCandles()), candleCount);
    }

    @Override
    public BigDecimal calculate(int index) {
        final BigDecimal highestHighPrice = highestValueIndicator.getValue(index);
        final BigDecimal lowestLowPrice = lowestValueIndicator.getValue(index);

        return indicator.getValue(index).subtract(lowestLowPrice)
                .divide(highestHighPrice.subtract(lowestLowPrice), 9, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
