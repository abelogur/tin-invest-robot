package ru.abelogur.tininvestrobot.indicator;

import ru.abelogur.tininvestrobot.domain.CachedCandle;

import java.util.List;

/**
 * Индикатор технического анализа над множеством {@link CachedCandle японских свечей}. <p/p> Для каждого индекса из
 * множества японских свечей возвращается значение типа <b>T</b>.
 *
 * @param <T> Возвращаемое значение (Double, Boolean, etc.)
 */

public interface Indicator<T> {

    /**
     * @param index индекса из свечей
     * @return значение индикатора в индексе
     */
    T getValue(int index);

    List<CachedCandle> getCandles();
}
