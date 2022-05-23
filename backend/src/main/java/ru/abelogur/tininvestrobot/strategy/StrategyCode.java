package ru.abelogur.tininvestrobot.strategy;

import lombok.Getter;
import ru.abelogur.tininvestrobot.domain.CachedCandle;
import ru.abelogur.tininvestrobot.dto.StrategiesConfig;
import ru.abelogur.tininvestrobot.dto.StrategyInfo;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.abelogur.tininvestrobot.strategy.OneMinuteScalpingStrategy.*;

@Getter
public enum StrategyCode {
    ONE_MINUTE_SCALPING("1-минутный скальпинг", Duration.ofMinutes(1), List.of(EMA50, EMA100, STOCHASTIC),
            (candles, config) -> Optional.ofNullable(config)
                    .map(StrategiesConfig::getOneMinuteScalpingConfig)
                    .map(it -> new OneMinuteScalpingStrategy(candles, it))
                    .orElseGet(() -> new OneMinuteScalpingStrategy(candles))),
    THREE_LINE_STRIKE("3 свечи", Duration.ofHours(1), Collections.emptyList(),
            (candles, config) -> Optional.ofNullable(config)
                    .map(StrategiesConfig::getThreeLineStrikeConfig)
                    .map(it -> new ThreeLineStrikeStrategy(candles, it))
                    .orElseGet(() -> new ThreeLineStrikeStrategy(candles)));

    private final String name;
    private final Duration interval;
    private final List<String> indicators;

    private final StrategyCreator creator;

    StrategyCode(String name, Duration interval, List<String> indicators, StrategyCreator creator) {
        this.name = name;
        this.interval = interval;
        this.indicators = indicators;
        this.creator = creator;
    }

    public StrategyInfo toDto() {
        return new StrategyInfo(this, name, interval, indicators);
    }

    public InvestStrategy create(List<CachedCandle> candles, StrategiesConfig config) {
        return this.creator.create(candles, config);
    }
}
