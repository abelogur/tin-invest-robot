package ru.abelogur.tininvestrobot.strategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Торговая стратегия
 */
public interface InvestStrategy {

    /**
     * Сигнал для открытия лонга
     */
    boolean isLongSignal();

    /**
     * Сигнал для открытия шорта
     */
    boolean isShortSignal();

    void setLastIndex(int index);

    StrategyCode getCode();

    /**
     * Получить значение индикаторов, которые используются в стратегии, на выбранном периоде.
     * При finish - start < 0 вернутся пустые массивы по каждому индикатору.
     * При finish - start = 0 вернется одно значение по каждому индикатору с index = finish = start
     *
     * @param start начало периода (включительно)
     * @param finish конец периода (включительно)
     * @return значение индикаторов, которые используются в стратегии, на выбранном периоде
     */
    Map<String, List<BigDecimal>> getValues(int start, int finish);
}
