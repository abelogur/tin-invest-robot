package ru.abelogur.tininvestrobot.helper;

import io.grpc.Status;
import lombok.SneakyThrows;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;

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

    public static Duration durationFrom(CandleInterval interval) {
        switch (interval) {
            case CANDLE_INTERVAL_1_MIN: return ChronoUnit.MINUTES.getDuration();
            case CANDLE_INTERVAL_5_MIN: return ChronoUnit.MINUTES.getDuration().multipliedBy(5);
            case CANDLE_INTERVAL_15_MIN: return ChronoUnit.MINUTES.getDuration().multipliedBy(15);
            case CANDLE_INTERVAL_HOUR: return ChronoUnit.WEEKS.getDuration();
            case CANDLE_INTERVAL_DAY: return ChronoUnit.YEARS.getDuration();
            default: throw new IllegalArgumentException("Invalid candle interval");
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

    public static Set<Status.Code> getStatusCodeToReconnect() {
        return Set.of(Status.Code.UNAVAILABLE, Status.Code.INTERNAL);
    }

    @SneakyThrows
    public static void delay(int millis) {
        Thread.sleep(millis);
    }
}
