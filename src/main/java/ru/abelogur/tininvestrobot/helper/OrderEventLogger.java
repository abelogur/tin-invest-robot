package ru.abelogur.tininvestrobot.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.service.OrderObserver;

import java.util.UUID;

@Slf4j
@Component
public class OrderEventLogger implements OrderObserver {

    @Override
    public void notifyOrder(UUID botUuid, Order order) {
        log.info(
                "Instrument {}. {} {} by reason {} at {}. price {}",
                order.getInstrument().getName(),
                order.getAction().name().toLowerCase(),
                order.getType().name().toLowerCase(),
                order.getReason().name().toLowerCase(),
                order.getTime(),
                order.getPrice()
        );
    }
}
