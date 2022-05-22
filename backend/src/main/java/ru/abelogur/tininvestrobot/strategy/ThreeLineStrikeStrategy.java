package ru.abelogur.tininvestrobot.strategy;

import lombok.Getter;
import ru.abelogur.tininvestrobot.domain.CachedCandle;
import ru.abelogur.tininvestrobot.indicator.LongCandleIndicator;
import ru.abelogur.tininvestrobot.indicator.helper.ClosePriceIndicator;
import ru.abelogur.tininvestrobot.indicator.helper.OpenPriceIndicator;
import ru.abelogur.tininvestrobot.strategy.config.ThreeLineStrikeConfig;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Три свечи
 *
 * @see <a href="https://tlc.thinkorswim.com/center/reference/Patterns/candlestick-patterns-library/bearish-and-bullish/ThreeLineStrike">
 * https://tlc.thinkorswim.com/center/reference/Patterns/candlestick-patterns-library/bearish-and-bullish/ThreeLineStrike</a>
 */
public class ThreeLineStrikeStrategy implements InvestStrategy {

    @Getter
    private final StrategyCode code = StrategyCode.THREE_LINE_STRIKE;

    private int lastIndex;

    private final OpenPriceIndicator openPriceIndicator;
    private final ClosePriceIndicator closePriceIndicator;
    private final LongCandleIndicator longCandleIndicator;

    public ThreeLineStrikeStrategy(List<CachedCandle> candles) {
        this(candles, new ThreeLineStrikeConfig());
    }

    public ThreeLineStrikeStrategy(List<CachedCandle> candles, ThreeLineStrikeConfig config) {
        this.lastIndex = candles.size() - 1;
        this.openPriceIndicator = new OpenPriceIndicator(candles);
        this.closePriceIndicator = new ClosePriceIndicator(candles);
        this.longCandleIndicator = new LongCandleIndicator(candles, config.getCandleLongSize());
    }

    @Override
    public boolean isLongSignal() {
        if (!previousThreeLong()) {
            return false;
        }

        var close1 = closePriceIndicator.getValue(lastIndex - 3);
        var close2 = closePriceIndicator.getValue(lastIndex - 2);
        var close3 = closePriceIndicator.getValue(lastIndex - 1);
        var close4 = closePriceIndicator.getValue(lastIndex);

        var open1 = openPriceIndicator.getValue(lastIndex - 3);
        var open2 = openPriceIndicator.getValue(lastIndex - 2);
        var open3 = openPriceIndicator.getValue(lastIndex - 1);
        var open4 = openPriceIndicator.getValue(lastIndex);

        return isDowntrend(close1, close2, close3)
                && openCandleInsidePrevious(close1, open1, open2)
                && openCandleInsidePrevious(close2, open2, open3)
                && open4.compareTo(close3) < 0 && close4.compareTo(open1) > 0;
    }

    @Override
    public boolean isShortSignal() {
        if (!previousThreeLong()) {
            return false;
        }

        var close1 = closePriceIndicator.getValue(lastIndex - 3);
        var close2 = closePriceIndicator.getValue(lastIndex - 2);
        var close3 = closePriceIndicator.getValue(lastIndex - 1);
        var close4 = closePriceIndicator.getValue(lastIndex);

        var open1 = openPriceIndicator.getValue(lastIndex - 3);
        var open2 = openPriceIndicator.getValue(lastIndex - 2);
        var open3 = openPriceIndicator.getValue(lastIndex - 1);
        var open4 = openPriceIndicator.getValue(lastIndex);

        return isUptrend(close1, close2, close3)
                && openCandleInsidePrevious(close1, open1, open2)
                && openCandleInsidePrevious(close2, open2, open3)
                && open4.compareTo(close3) > 0 && close4.compareTo(open1) < 0;
    }

    @Override
    public void setLastIndex(int index) {
        lastIndex = index;
    }

    @Override
    public Map<String, List<BigDecimal>> getValues(int start, int finish) {
        return Collections.emptyMap();
    }

    private boolean previousThreeLong() {
        return longCandleIndicator.getValue(lastIndex - 3)
                && longCandleIndicator.getValue(lastIndex - 2)
                && longCandleIndicator.getValue(lastIndex - 1);
    }

    private boolean isDowntrend(BigDecimal p1, BigDecimal p2, BigDecimal p3) {
        return p1.compareTo(p2) > 0 && p2.compareTo(p3) > 0;
    }

    private boolean isUptrend(BigDecimal p1, BigDecimal p2, BigDecimal p3) {
        return p1.compareTo(p2) < 0 && p2.compareTo(p3) < 0;
    }

    private boolean openCandleInsidePrevious(BigDecimal closePrev, BigDecimal openPrev, BigDecimal currentOpen) {
        return currentOpen.compareTo(closePrev.min(currentOpen)) > 0
                && currentOpen.compareTo(openPrev.max(closePrev)) < 0;
    }
}
