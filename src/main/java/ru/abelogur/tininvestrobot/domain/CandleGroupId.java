package ru.abelogur.tininvestrobot.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

@Getter
@EqualsAndHashCode
public class CandleGroupId {
    private String figi;
    private String interval;

    public CandleInterval getCandleInterval() {
        return CandleInterval.valueOf(interval);
    }

    private CandleGroupId(String figi, String interval) {
        this.figi = figi;
        this.interval = interval;
    }

    public static CandleGroupId of(String figi, CandleInterval interval) {
        return new CandleGroupId(figi, interval.toString());
    }
}
