package ru.abelogur.tininvestrobot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.abelogur.tininvestrobot.strategy.StrategyCode;

import java.time.Duration;
import java.util.List;

@Getter
@AllArgsConstructor
public class StrategyInfo {
    @Schema(description = "Код стратегии")
    private StrategyCode strategyCode;

    @Schema(description = "Название стратегии")
    private String name;

    @Schema(description = "Интервал, на котором работает стратегия (если есть)")
    private Duration interval;

    @Schema(description = "Индикаторы, которые используются в стратегии")
    private List<String> indicators;
}
