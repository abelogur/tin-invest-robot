package ru.abelogur.tininvestrobot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.domain.TradeType;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
public class OrderService {

    public Optional<Order> buyLong(String figi) {
        log.info("Buy long {}", figi);
        return Optional.of(new Order(TradeType.LONG, BigDecimal.ONE));
    }

    public boolean sellLong(String figi) {
        log.info("Sell long {}", figi);
        return true;
    }

    public Optional<Order> buyShort(String figi) {
        log.info("Buy long {}", figi);
        return Optional.of(new Order(TradeType.SHORT, BigDecimal.ONE));
    }

    public boolean sellShort(String figi) {
        log.info("Buy long {}", figi);
        return true;
    }
}
