package ru.abelogur.tininvestrobot.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.abelogur.tininvestrobot.dto.OrderMetadata;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Order {
    private TradeType type;
    private BigDecimal price;
    private BigDecimal commission;
    private Instant time;
    private OrderReason reason;
    private OrderAction action;
    private CachedInstrument instrument;

    public boolean isLong() {
        return type.equals(TradeType.LONG);
    }

    public boolean isShort() {
        return type.equals(TradeType.SHORT);
    }

    public static Order of(TradeType tradeType, OrderAction action, BigDecimal commission,
                           OrderMetadata metadata, CachedInstrument instrument) {
        return new Order(tradeType, metadata.getPrice(), commission, metadata.getTime(),
                metadata.getReason(), action, instrument);
    }
}
