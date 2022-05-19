package ru.abelogur.tininvestrobot.strategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface InvestStrategy {

    boolean isLongSignal();

    boolean isShortSignal();

    void setLastIndex(int index);

    StrategyCode getCode();

    Map<String, List<BigDecimal>> getValues(int start, int end);
}
