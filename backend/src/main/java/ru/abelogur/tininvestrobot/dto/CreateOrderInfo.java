package ru.abelogur.tininvestrobot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.abelogur.tininvestrobot.domain.OrderReason;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateOrderInfo {
    private UUID botUuid;
    private String figi;
    private OrderReason reason;
    private BigDecimal price;
    private Instant time;
    private String accountId;
    private int numberOfLots;
}
