package ru.abelogur.tininvestrobot.strategy.config;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ThreeLineStrikeConfig {
    private BigDecimal candleLongSize = BigDecimal.valueOf(0.0005);
}
