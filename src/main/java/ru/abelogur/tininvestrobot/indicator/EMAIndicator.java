package ru.abelogur.tininvestrobot.indicator;

import ru.abelogur.tininvestrobot.indicator.helper.ClosePriceIndicator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Экспоненциальное скользящее среднее
 * @see <a href="https://www.investopedia.com/terms/e/ema.asp">https://www.investopedia.com/terms/e/ema.asp</a>
 */
public class EMAIndicator extends CachedIndicator<BigDecimal> {

    private final ClosePriceIndicator indicator;
    private final BigDecimal multiplier;

    public EMAIndicator(List<IndicatorCandle> candles, int candlesCount) {
        this(new ClosePriceIndicator(candles), candlesCount);
    }

    public EMAIndicator(ClosePriceIndicator indicator, int candleCount) {
        super(indicator);
        this.indicator = indicator;
        this.multiplier = BigDecimal.valueOf(2.0 / (candleCount + 1));

        // Значения для индикатора вычисляются на основе предыдущих значений рекурсивно.
        // Чтобы избежать StackOverflowError стоить постепенно заполнить кэш.
        warmCache();
    }

    @Override
    protected BigDecimal calculate(int index) {
        if (index == 0) {
            return indicator.getValue(0);
        }
        BigDecimal prevValue = getValue(index - 1);
        return indicator.getValue(index)
                .subtract(prevValue)
                .multiply(multiplier)
                .add(prevValue).setScale(9, RoundingMode.HALF_UP);
    }

    private void warmCache() {
        int i = 0;
        while (i + 10 < getCandles().size()) {
            i += 10;
            getValue(i);
        }
    }
}
