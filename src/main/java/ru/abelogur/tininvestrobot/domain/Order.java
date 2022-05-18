package ru.abelogur.tininvestrobot.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Order {
    private String id;
    private UUID botUuid;
    private TradeType type;
    private BigDecimal price;
    private BigDecimal commission;
    private Instant time;
    private OrderReason reason;
    private OrderAction action;
    private OrderStatus status;
    private String instrumentName;

    public boolean isLong() {
        return type.equals(TradeType.LONG);
    }

    public boolean isShort() {
        return type.equals(TradeType.SHORT);
    }

    public boolean isNew() {
        return status.equals(OrderStatus.NEW);
    }

    public void updateOrder(Order order) {
        updateOrder(order.getStatus(), order.getPrice(), order.getCommission());
    }

    public void updateOrder(OrderStatus status, BigDecimal price, BigDecimal commission) {
        this.status = status;
        this.price = price;
        this.commission = commission;
    }
}
