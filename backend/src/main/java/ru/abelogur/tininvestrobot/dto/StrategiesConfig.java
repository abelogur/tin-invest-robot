package ru.abelogur.tininvestrobot.dto;

import lombok.Getter;
import ru.abelogur.tininvestrobot.strategy.config.OneMinuteScalpingConfig;

@Getter
public class StrategiesConfig {
    private OneMinuteScalpingConfig oneMinuteScalpingConfig;
}
