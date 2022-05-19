package ru.abelogur.tininvestrobot.strategy;

import lombok.Getter;
import ru.abelogur.tininvestrobot.domain.CachedCandle;
import ru.abelogur.tininvestrobot.indicator.EMAIndicator;
import ru.abelogur.tininvestrobot.indicator.SMAIndicator;
import ru.abelogur.tininvestrobot.indicator.StochasticOscillator;
import ru.abelogur.tininvestrobot.indicator.helper.ClosePriceIndicator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OneMinuteScalpingStrategy implements InvestStrategy {

    public static final String EMA50 = "EMA50";
    public static final String EMA100 = "EMA100";
    public static final String STOCHASTIC = "Stochastic";

    @Getter
    private final StrategyCode code = StrategyCode.ONE_MINUTE_SCALPING;

    private final int STOCHASTIC_LENGTH = 5;
    private final int STOCHASTIC_SMOOTHING = 3;

    private int lastIndex;

    @Getter
    private final ClosePriceIndicator closePriceIndicator;
    @Getter
    private final EMAIndicator ema50Indicator;
    @Getter
    private final EMAIndicator ema100Indicator;
    @Getter
    private final SMAIndicator slowStochastic;

    public OneMinuteScalpingStrategy(List<CachedCandle> candles) {
        this.lastIndex = candles.size() - 1;
        this.closePriceIndicator = new ClosePriceIndicator(candles);
        this.ema50Indicator = new EMAIndicator(candles, 50);
        this.ema100Indicator = new EMAIndicator(candles, 100);
        this.slowStochastic = new SMAIndicator(new StochasticOscillator(candles, STOCHASTIC_LENGTH), STOCHASTIC_SMOOTHING);
    }

    @Override
    public boolean isLongSignal() {
        return isEma50AboveEma100() && isPriceNearEma100() && isStochasticLowToHigh();
    }

    @Override
    public boolean isShortSignal() {
        return isEma50BelowEma100() && isPriceNearEma100() && isStochasticHighToLow();
    }

    @Override
    public void setLastIndex(int index) {
        lastIndex = index;
    }

    @Override
    public Map<String, List<BigDecimal>> getValues(int start, int finish) {
        Map<String, List<BigDecimal>> result = new HashMap<>();
        result.put(EMA50, new ArrayList<>());
        result.put(EMA100, new ArrayList<>());
        result.put(STOCHASTIC, new ArrayList<>());
        for (int i = 0; i <= finish; i++) {
            result.get(EMA50).add(ema50Indicator.getValue(i));
            result.get(EMA100).add(ema100Indicator.getValue(i));
            result.get(STOCHASTIC).add(slowStochastic.getValue(i));
        }
        return result;
    }

    private boolean isEma50AboveEma100() {
        return ema50Indicator.getValue(lastIndex).doubleValue() > ema100Indicator.getValue(lastIndex).doubleValue();
    }

    private boolean isStochasticLowToHigh() {
        var lastStochastic = slowStochastic.getValue(lastIndex - 1).doubleValue();
        var currentStochastic = slowStochastic.getValue(lastIndex).doubleValue();
        return lastStochastic < 20 && currentStochastic > 25 && currentStochastic < 40;
    }

    private boolean isEma50BelowEma100() {
        return ema50Indicator.getValue(lastIndex).doubleValue() < ema100Indicator.getValue(lastIndex).doubleValue();
    }

    private boolean isPriceNearEma100() {
        var closePrice = closePriceIndicator.getValue(lastIndex).doubleValue();
        return Math.abs(closePrice - ema100Indicator.getValue(lastIndex).doubleValue()) < closePrice * 0.0005;
    }

    private boolean isStochasticHighToLow() {
        var lastStochastic = slowStochastic.getValue(lastIndex - 1).doubleValue();
        var currentStochastic = slowStochastic.getValue(lastIndex).doubleValue();
        return lastStochastic > 80 && currentStochastic < 75 && currentStochastic > 60;
    }
}
