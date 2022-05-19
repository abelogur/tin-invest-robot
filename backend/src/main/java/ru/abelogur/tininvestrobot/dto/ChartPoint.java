package ru.abelogur.tininvestrobot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.abelogur.tininvestrobot.domain.CachedCandle;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class ChartPoint {
    public BigDecimal value;
    public Instant time;

    public static ChartPoint of(CachedCandle candle) {
        return new ChartPoint(candle.getClosePrice(), candle.getTime());
    }
}
