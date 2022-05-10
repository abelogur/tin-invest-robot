package ru.abelogur.tininvestrobot.util;

import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.time.Duration;

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
}
