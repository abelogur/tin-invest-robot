package ru.abelogur.tininvestrobot.strategy.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static ru.abelogur.tininvestrobot.strategy.OneMinuteScalpingStrategy.DEFAULT_STOCHASTIC_LENGTH;
import static ru.abelogur.tininvestrobot.strategy.OneMinuteScalpingStrategy.DEFAULT_STOCHASTIC_SMOOTHING;

@Getter
@Setter
@Accessors(chain = true)
public class OneMinuteScalpingConfig {
    private Integer stochasticLength = DEFAULT_STOCHASTIC_LENGTH;
    private Integer stochasticSmoothing = DEFAULT_STOCHASTIC_SMOOTHING;
}
