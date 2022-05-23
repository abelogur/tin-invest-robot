package ru.abelogur.tininvestrobot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.abelogur.tininvestrobot.domain.OrderError;
import ru.abelogur.tininvestrobot.strategy.StrategyCode;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class BotPreview {
    @Schema(description = "Идентификатор бота")
    private UUID uuid;

    @Schema(description = "Дата создания бота")
    private Instant start;

    @Schema(description = "Выбранная стратегия")
    private StrategyCode strategyCode;

    @Schema(description = "На каком окружении был запущен бот")
    private BotEnv botEnv;

    @Schema(description = "Инструмент, с которым работает бот")
    private String instrument;

    @Schema(description = "Тикет инструмента на бирже")
    private String instrumentTicket;

    @Schema(description = "Кол-во запросов, совершенные ботом")
    private Integer numberOfOrders;

    @Schema(description = "Доход бота")
    private BigDecimal profit;

    @Schema(description = "Доход бота в процентах")
    private BigDecimal profitPercentage;

    @Schema(description = "Валюта инструмента")
    private String currency;

    @Schema(description = "chatId с телеграм ботом (если есть)")
    private String telegramBotChatId;

    @Schema(description = "Предполагаемый url картинки (может быть некорректным)")
    private String iconUrl;

    @Schema(description = "Ошибки, возникающие при работе с заявками")
    private List<OrderError> errors = new ArrayList<>();
}
