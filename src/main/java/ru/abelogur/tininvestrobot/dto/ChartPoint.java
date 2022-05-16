package ru.abelogur.tininvestrobot.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Accessors(chain = true)
public class ChartPoint {
    public BigDecimal value;
    public Instant time;

    public static ChartPoint from(BigDecimal value) {
        return new ChartPoint().setValue(value);
    }
}
