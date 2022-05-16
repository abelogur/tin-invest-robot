package ru.abelogur.tininvestrobot.helper;

import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public final class HelperUtils {

    public static CandleInterval intervalFrom(Duration interval) {
        if (interval.toMinutes() == 1) {
            return CandleInterval.CANDLE_INTERVAL_1_MIN;
        } else if (interval.toMinutes() == 5) {
            return CandleInterval.CANDLE_INTERVAL_5_MIN;
        } else if (interval.toMinutes() == 15) {
            return CandleInterval.CANDLE_INTERVAL_15_MIN;
        } else if (interval.toHours() == 1) {
            return CandleInterval.CANDLE_INTERVAL_HOUR;
        } else if (interval.toDays() == 1) {
            return CandleInterval.CANDLE_INTERVAL_DAY;
        } else {
            return CandleInterval.CANDLE_INTERVAL_UNSPECIFIED;
        }
    }

    public static Duration getMaxRequestPeriod(CandleInterval interval) {
        switch (interval) {
            case CANDLE_INTERVAL_1_MIN:
            case CANDLE_INTERVAL_5_MIN:
            case CANDLE_INTERVAL_15_MIN: return ChronoUnit.DAYS.getDuration();
            case CANDLE_INTERVAL_HOUR: return ChronoUnit.WEEKS.getDuration();
            case CANDLE_INTERVAL_DAY: return ChronoUnit.YEARS.getDuration();
            default: throw new IllegalArgumentException("Invalid candle interval");
        }
    }
}
