package ru.abelogur.tininvestrobot.indicator;

import ru.abelogur.tininvestrobot.domain.CachedCandle;

import java.util.List;

/**
 * Индикатор технического анализа над множеством {@link CachedCandle японских свечей}. <p/p> Для каждого индекса из
 * множества японских свечей возвращается значение типа <b>T</b>.
 *
 * @param <T> Возвращаемое значение (BigDecimal, Boolean, etc.)
 */

public interface Indicator<T> {

    /**
     * @param index индекс из свечей
     * @return значение индикатора в индексе
     */
    T getValue(int index);

    /**
     * @return получение всех свечей в индикаторе
     */
    List<CachedCandle> getCandles();
}
