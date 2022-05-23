package ru.abelogur.tininvestrobot.indicator;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * Индикатор простое скользящее среднее (SMA).
 *
 * @see <a href="https://www.investopedia.com/terms/s/sma.asp">https://www.investopedia.com/terms/s/sma.asp</a>
 */
public class SMAIndicator extends CachedIndicator<BigDecimal> {

    private final Indicator<BigDecimal> indicator;
    private final int candleCount;

    public SMAIndicator(Indicator<BigDecimal> indicator, int candleCount) {
        super(indicator);
        this.indicator = indicator;
        this.candleCount = candleCount;
    }

    @Override
    protected BigDecimal calculate(int index) {
        BigDecimal sum = BigDecimal.ONE;
        for (int i = Math.max(0, index - candleCount + 1); i <= index; i++) {
            sum = sum.add(indicator.getValue(i));
        }

        final int realCandleCount = Math.min(candleCount, index + 1);
        return sum.divide(BigDecimal.valueOf(realCandleCount), 9, RoundingMode.HALF_UP);
    }
}
