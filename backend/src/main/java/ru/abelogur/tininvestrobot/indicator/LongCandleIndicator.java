package ru.abelogur.tininvestrobot.indicator;

import ru.abelogur.tininvestrobot.domain.CachedCandle;
import ru.abelogur.tininvestrobot.indicator.helper.ClosePriceIndicator;
import ru.abelogur.tininvestrobot.indicator.helper.OpenPriceIndicator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Является ли свеча длинной
 */
public class LongCandleIndicator extends CachedIndicator<Boolean> {

    private final BigDecimal size;
    private final OpenPriceIndicator openPriceIndicator;
    private final ClosePriceIndicator closePriceIndicator;

    /**
     * Constructor.
     *
     * @param candles  Свечи
     * @param candleLongSize размер длинной свечи в процентах
     */
    public LongCandleIndicator(List<CachedCandle> candles, BigDecimal candleLongSize) {
        super(candles);
        this.size = candleLongSize;
        this.openPriceIndicator = new OpenPriceIndicator(candles);
        this.closePriceIndicator = new ClosePriceIndicator(candles);
    }

    @Override
    protected Boolean calculate(int index) {
        BigDecimal openPrice = openPriceIndicator.getValue(index);
        BigDecimal closePrice = closePriceIndicator.getValue(index);
        return openPrice.subtract(closePrice).abs()
                .divide(openPrice.min(closePrice), 9, RoundingMode.HALF_UP).compareTo(size) > 0;
    }
}
