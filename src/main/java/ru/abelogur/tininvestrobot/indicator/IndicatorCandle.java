package ru.abelogur.tininvestrobot.indicator;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class IndicatorCandle implements Comparable<IndicatorCandle> {
    private BigDecimal closePrice;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private OffsetDateTime time;

    @Override
    public int compareTo(IndicatorCandle ic) {
        return this.getTime().compareTo(ic.getTime());
    }
}
