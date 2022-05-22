package ru.abelogur.tininvestrobot.strategy;

import ru.abelogur.tininvestrobot.domain.CachedCandle;
import ru.abelogur.tininvestrobot.dto.StrategiesConfig;

import java.util.List;

@FunctionalInterface
public interface StrategyCreator {
    InvestStrategy create(List<CachedCandle> candles, StrategiesConfig config);
}
