package ru.abelogur.tininvestrobot.dto;

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
    private UUID uuid;
    private Instant start;
    private StrategyCode strategyCode;
    private BotEnv botEnv;
    private String instrument;
    private String instrumentTicket;
    private Integer numberOfOrders;
    private BigDecimal profit;
    private BigDecimal profitPercentage;
    private String currency;
    private String telegramBotChatId;
    private String iconUrl;
    private List<OrderError> errors = new ArrayList<>();
}
