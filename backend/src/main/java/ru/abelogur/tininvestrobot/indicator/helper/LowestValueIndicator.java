package ru.abelogur.tininvestrobot.indicator.helper;

import ru.abelogur.tininvestrobot.indicator.CachedIndicator;

import java.math.BigDecimal;

/**
 * Индикатор минимального значения в последних(candleCount) свечах
 */
public class LowestValueIndicator extends CachedIndicator<BigDecimal> {

    private final PriceIndicator indicator;
    private final int candleCount;

    public LowestValueIndicator(PriceIndicator indicator, int candleCount) {
        super(indicator);
        this.indicator = indicator;
        this.candleCount = candleCount;
    }

    @Override
    protected BigDecimal calculate(int index) {
        int end = Math.max(0, index - candleCount + 1);
        BigDecimal lowest = indicator.getValue(index);
        for (int i = index - 1; i >= end; i--) {
            if (lowest.compareTo(indicator.getValue(i)) > 0) {
                lowest = indicator.getValue(i);
            }
        }
        return lowest;
    }
}
