package ru.abelogur.tininvestrobot.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.SubscriptionInterval;

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

    public static CandleGroupId of(String figi, SubscriptionInterval subscriptionInterval) {
        String interval = CandleInterval.UNRECOGNIZED.toString();
        if (subscriptionInterval.equals(SubscriptionInterval.SUBSCRIPTION_INTERVAL_ONE_MINUTE)) {
            interval = CandleInterval.CANDLE_INTERVAL_1_MIN.toString();
        } else if (subscriptionInterval.equals(SubscriptionInterval.SUBSCRIPTION_INTERVAL_FIVE_MINUTES)) {
            interval = CandleInterval.CANDLE_INTERVAL_5_MIN.toString();
        }
        return new CandleGroupId(figi, interval);
    }
}