package ru.abelogur.tininvestrobot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.abelogur.tininvestrobot.strategy.StrategyCode;

import java.time.Duration;
import java.util.List;

@Getter
@AllArgsConstructor
public class StrategyInfo {
    private StrategyCode strategyCode;
    private String name;
    private Duration interval;
    private List<String> indicators;
}
