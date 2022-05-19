package ru.abelogur.tininvestrobot.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class BotPreview {
    private UUID id;
    private Instant start;
    private String strategy;
    private BotType botType;
    private String instrument;
    private String instrumentTicket;
    private Integer numberOfOrders;
    private BigDecimal profit;
    private BigDecimal profitPercentage;
    private String currency;
}
