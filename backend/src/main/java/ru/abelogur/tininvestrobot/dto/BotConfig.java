package ru.abelogur.tininvestrobot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import ru.abelogur.tininvestrobot.strategy.StrategyCode;

import java.math.BigDecimal;

@Getter
public class BotConfig {
    @Schema(description = "figi инструмента", required = true)
    private String figi;

    @Schema(description = "Настройка take profit'а", defaultValue = "0.0008")
    private BigDecimal takeProfit = BigDecimal.valueOf(0.0008);

    @Schema(description = "Настройка stop loss'а", defaultValue = "0.0003")
    private BigDecimal stopLoss = BigDecimal.valueOf(0.0003);

    @Schema(description = "С какого аккаунта у брокера будет вестись торговля. " +
            "При отсутствии будет выбран первый найденный аккаунт с подходящими доступами")
    private String accountId;

    @Schema(description = "Количество лотов на одну заявку", defaultValue = "1")
    private Integer numberOfLots = 1;

    @Schema(description = "Стратегия, по которой бот будет работать", defaultValue = "ONE_MINUTE_SCALPING")
    private StrategyCode strategyCode = StrategyCode.ONE_MINUTE_SCALPING;

    @Schema(description = "Уведомления о сделках будут приходить в этот чат")
    private String telegramBotChatId;

    @Schema(description = "Конфигурации для стратегий")
    private StrategiesConfig strategiesConfig;
}
