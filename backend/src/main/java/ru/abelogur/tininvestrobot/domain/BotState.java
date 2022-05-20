package ru.abelogur.tininvestrobot.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.abelogur.tininvestrobot.dto.BotEnv;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class BotState {
    private UUID uuid;
    private CandleGroupId groupId;
    private Instant initTime;
    private String accountId;
    private BotEnv botEnv;
    private BigDecimal takeProfit;
    private BigDecimal stopLoss;
    private int numberOfLots = 1;
    private boolean marginAvailable = false;

    private List<OrderError> errors = new ArrayList<>();

    public String getFigi() {
        return groupId.getFigi();
    }
}
