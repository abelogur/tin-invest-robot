package ru.abelogur.tininvestrobot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.domain.TradeType;
import ru.abelogur.tininvestrobot.dto.OrderMetadata;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
public class OrderService {

    private static BigDecimal balance = BigDecimal.ZERO;

    public Optional<Order> buyLong(String figi, OrderMetadata metadata) {
        balance = balance.subtract(metadata.getPrice());
        log.info("Buy long {}. Reason {}. Candle time {}. Price {}. Balance {}", figi, metadata.getReason(), metadata.getTime(), metadata.getPrice(), balance);
        return Optional.of(new Order(TradeType.LONG, metadata.getPrice()));
    }

    public boolean sellLong(String figi, OrderMetadata metadata) {
        balance = balance.add(metadata.getPrice());
        log.info("Sell long {}. Reason {}. Candle time {}. Price {}. Balance {}", figi, metadata.getReason(), metadata.getTime(), metadata.getPrice(), balance);
        return true;
    }

    public Optional<Order> buyShort(String figi, OrderMetadata metadata) {
        balance = balance.subtract(metadata.getPrice());
        log.info("Buy short {}. Reason {}. Candle time {}. Price {}. Balance {}", figi, metadata.getReason(), metadata.getTime(), metadata.getPrice(), balance);
        return Optional.of(new Order(TradeType.SHORT, metadata.getPrice()));
    }

    public boolean sellShort(String figi, OrderMetadata metadata) {
        balance = balance.add(metadata.getPrice());
        log.info("Sell short {}. Reason {}. Candle time {}. Price {}. Balance {}", figi, metadata.getReason(), metadata.getTime(), metadata.getPrice(), balance);
        return true;
    }
}
