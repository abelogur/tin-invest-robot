package ru.abelogur.tininvestrobot.controller.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class BotConfig {
    private String figi;
    private BigDecimal takeProfit = BigDecimal.valueOf(0.15);
    private BigDecimal stopLoss = BigDecimal.valueOf(0.05);
}
