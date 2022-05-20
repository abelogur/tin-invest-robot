package ru.abelogur.tininvestrobot.dto;

import lombok.Getter;
import ru.abelogur.tininvestrobot.strategy.StrategyCode;

import java.math.BigDecimal;

@Getter
public class BotConfig {
    private String figi;
    private BigDecimal takeProfit = BigDecimal.valueOf(0.0008);
    private BigDecimal stopLoss = BigDecimal.valueOf(0.0003);
    private String accountId;
    private Integer numberOfLots = 1;
    private StrategyCode strategyCode = StrategyCode.ONE_MINUTE_SCALPING;
    private String telegramBotChatId;

    private StrategiesConfig strategiesConfig;
}
