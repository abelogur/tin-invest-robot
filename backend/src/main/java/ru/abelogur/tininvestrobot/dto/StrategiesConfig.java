package ru.abelogur.tininvestrobot.dto;

import lombok.Getter;
import ru.abelogur.tininvestrobot.strategy.config.OneMinuteScalpingConfig;
import ru.abelogur.tininvestrobot.strategy.config.ThreeLineStrikeConfig;

@Getter
public class StrategiesConfig {
    private OneMinuteScalpingConfig oneMinuteScalpingConfig;
    private ThreeLineStrikeConfig threeLineStrikeConfig;
}
