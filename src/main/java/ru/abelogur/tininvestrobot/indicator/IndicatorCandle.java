package ru.abelogur.tininvestrobot.indicator;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class IndicatorCandle implements Comparable {
    private BigDecimal closePrice;
    private OffsetDateTime time;

    @Override
    public int compareTo(Object o) {
        IndicatorCandle ic = (IndicatorCandle) o;
        return this.getTime().compareTo(ic.getTime());
    }
}
