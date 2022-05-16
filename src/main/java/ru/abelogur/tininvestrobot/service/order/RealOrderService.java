package ru.abelogur.tininvestrobot.service.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.dto.OrderMetadata;

import java.util.Optional;

@Slf4j
@Service
public class RealOrderService implements OrderService {

    public Optional<Order> buyLong(String figi, OrderMetadata metadata) {
        log.info("Buy long {}. Reason {}. Candle time {}. Price {}. Balance {}", figi, metadata.getReason(), metadata.getTime(), metadata.getPrice());
        return Optional.empty();
    }

    public boolean sellLong(String figi, OrderMetadata metadata) {
        log.info("Sell long {}. Reason {}. Candle time {}. Price {}. Balance {}", figi, metadata.getReason(), metadata.getTime(), metadata.getPrice());
        return true;
    }

    public Optional<Order> buyShort(String figi, OrderMetadata metadata) {
        log.info("Buy short {}. Reason {}. Candle time {}. Price {}. Balance {}", figi, metadata.getReason(), metadata.getTime(), metadata.getPrice());
        return Optional.empty();
    }

    public boolean sellShort(String figi, OrderMetadata metadata) {
        log.info("Sell short {}. Reason {}. Candle time {}. Price {}. Balance {}", figi, metadata.getReason(), metadata.getTime(), metadata.getPrice());
        return true;
    }
}
