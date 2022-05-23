package ru.abelogur.tininvestrobot.service.notification.socket;

import lombok.Getter;

@Getter
public enum IndicatorEvents {
    INDICATORS_SUBSCRIBE("indicators-subscribe"),
    INDICATORS("indicators");

    private final String topic;

    IndicatorEvents(String topic) {
        this.topic = topic;
    }
}
