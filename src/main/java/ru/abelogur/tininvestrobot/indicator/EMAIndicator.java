package ru.abelogur.tininvestrobot.indicator;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Экспоненциальное скользящее среднее
 * @see <a href="https://www.investopedia.com/terms/e/ema.asp">https://www.investopedia.com/terms/e/ema.asp</a>
 */
public class EMAIndicator implements Indicator<BigDecimal> {

    @Getter
    private List<IndicatorCandle> candles;
    private BigDecimal multiplier;

    private final Map<Integer, BigDecimal> cacheValues = new HashMap<>();

    public EMAIndicator(List<IndicatorCandle> candles, Integer candlesCount) {
        this.candles = candles;
        this.multiplier = BigDecimal.valueOf(2.0 / (candlesCount + 1));

        warmCache();
    }

    @Override
    public BigDecimal getValue(int index) {
        if (index == 0) {
            return candles.get(0).getClosePrice();
        }
        if (cacheValues.containsKey(index)) {
            return cacheValues.get(index);
        }

        BigDecimal prevValue = getValue(index - 1);
        var value = candles.get(index).getClosePrice().subtract(prevValue)
                .multiply(multiplier).add(prevValue).setScale(9, RoundingMode.HALF_UP);
        cacheValues.put(index, value);
        return value;
    }

    private void warmCache() {
        int i = 0;
        while (i + 10 < candles.size()) {
            i += 10;
            getValue(i);
        }
    }
}
