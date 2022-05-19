package ru.abelogur.tininvestrobot.strategy;

import lombok.Getter;
import ru.abelogur.tininvestrobot.domain.CachedCandle;
import ru.abelogur.tininvestrobot.dto.StrategiesConfig;
import ru.abelogur.tininvestrobot.dto.StrategyInfo;

import java.time.Duration;
import java.util.List;

import static ru.abelogur.tininvestrobot.strategy.OneMinuteScalpingStrategy.*;

@Getter
public enum StrategyCode {
    ONE_MINUTE_SCALPING("1-минутный скальпинг", Duration.ofMinutes(1), List.of(EMA50, EMA100, STOCHASTIC));

    private final String name;
    private final Duration interval;
    private final List<String> indicators;

    StrategyCode(String name, Duration interval, List<String> indicators) {
        this.name = name;
        this.interval = interval;
        this.indicators = indicators;
    }

    public StrategyInfo toDto() {
        return new StrategyInfo(this, name, interval, indicators);
    }

    public InvestStrategy create(List<CachedCandle> candles, StrategiesConfig config) {
        if (config == null || config.getOneMinuteScalpingConfig() == null) {
            return new OneMinuteScalpingStrategy(candles);
        } else {
            return new OneMinuteScalpingStrategy(candles, config.getOneMinuteScalpingConfig());
        }
    }
}
