package ru.abelogur.tininvestrobot.domain;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Order {
    private TradeType type;
    private BigDecimal openPrice;

    public boolean isLong() {
        return type.equals(TradeType.LONG);
    }

    public boolean isShort() {
        return type.equals(TradeType.SHORT);
    }
}
