package ru.abelogur.tininvestrobot.strategy;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum StrategyCode {
    ONE_MINUTE_SCALPING("1-минутный скальпинг", Duration.ofMinutes(1));

    private String name;
    private Duration interval;

    StrategyCode(String name, Duration interval) {
        this.name = name;
        this.interval = interval;
    }
}
