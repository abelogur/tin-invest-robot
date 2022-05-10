package ru.abelogur.tininvestrobot.controller.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.abelogur.tininvestrobot.indicator.IndicatorCandle;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class ChartPoint {
    public BigDecimal value;
    public OffsetDateTime time;

    public static ChartPoint from(BigDecimal value) {
        return new ChartPoint().setValue(value);
    }
}
