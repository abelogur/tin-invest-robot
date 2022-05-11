package ru.abelogur.tininvestrobot.strategy;

import ru.abelogur.tininvestrobot.indicator.EMAIndicator;
import ru.abelogur.tininvestrobot.indicator.IndicatorCandle;
import ru.abelogur.tininvestrobot.indicator.SMAIndicator;
import ru.abelogur.tininvestrobot.indicator.StochasticOscillator;
import ru.abelogur.tininvestrobot.indicator.helper.ClosePriceIndicator;

import java.util.List;

public class OneMinuteScalpingStrategy implements InvestStrategy {

    private final int STOCHASTIC_LENGTH = 5;
    private final int STOCHASTIC_SMOOTHING = 3;

    private int lastIndex;
    private final ClosePriceIndicator closePriceIndicator;
    private final EMAIndicator ema50Indicator;
    private final EMAIndicator ema100Indicator;
    private final SMAIndicator slowStochastic;

    public OneMinuteScalpingStrategy(List<IndicatorCandle> candles) {
        this.lastIndex = candles.size() - 1;
        this.closePriceIndicator = new ClosePriceIndicator(candles);
        this.ema50Indicator = new EMAIndicator(candles, 50);
        this.ema100Indicator = new EMAIndicator(candles, 100);
        this.slowStochastic = new SMAIndicator(new StochasticOscillator(candles, STOCHASTIC_LENGTH), STOCHASTIC_SMOOTHING);
    }

    @Override
    public boolean isLongSignal() {
        return isEma50AboveEma100() && isPriceBelowEma100() && isStochasticLowToHigh();
    }

    @Override
    public boolean isShortSignal() {
        return isEma50BelowEma100() && isPriceAboveEma100() && isStochasticHighToLow();
    }

    private boolean isEma50AboveEma100() {
        return ema50Indicator.getValue(lastIndex).doubleValue() > ema100Indicator.getValue(lastIndex).doubleValue();
    }

    private boolean isPriceBelowEma100() {
        return closePriceIndicator.getValue(lastIndex).doubleValue() < ema100Indicator.getValue(lastIndex).doubleValue();
    }

    private boolean isStochasticLowToHigh() {
        var minRecentlyStochastic = Math.min(
                slowStochastic.getValue(lastIndex - 1).doubleValue(),
                slowStochastic.getValue(lastIndex - 2).doubleValue()
        );
        var currentStochastic = slowStochastic.getValue(lastIndex).doubleValue();
        return minRecentlyStochastic < 20 && currentStochastic > 25 && currentStochastic < 40;
    }

    private boolean isEma50BelowEma100() {
        return ema50Indicator.getValue(lastIndex).doubleValue() < ema100Indicator.getValue(lastIndex).doubleValue();
    }

    private boolean isPriceAboveEma100() {
        return closePriceIndicator.getValue(lastIndex).doubleValue() > ema100Indicator.getValue(lastIndex).doubleValue();
    }

    private boolean isStochasticHighToLow() {
        var maxRecentlyStochastic = Math.max(
                slowStochastic.getValue(lastIndex - 1).doubleValue(),
                slowStochastic.getValue(lastIndex - 2).doubleValue()
        );
        var currentStochastic = slowStochastic.getValue(lastIndex).doubleValue();
        return maxRecentlyStochastic > 80 && currentStochastic < 75 && currentStochastic > 60;
    }
}
