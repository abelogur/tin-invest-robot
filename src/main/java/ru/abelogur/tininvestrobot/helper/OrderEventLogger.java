package ru.abelogur.tininvestrobot.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.service.OrderObserver;

@Slf4j
@Component
public class OrderEventLogger implements OrderObserver {

    @Override
    public void notifyNewOrder(Order order) {
        log.info(
                "Order is opened. Instrument {}. {} {} by reason {} at {}. price {}",
                order.getInstrumentName(),
                order.getAction().name().toLowerCase(),
                order.getType().name().toLowerCase(),
                order.getReason().name().toLowerCase(),
                order.getTime(),
                order.getPrice()
        );
    }

    @Override
    public void notifySuccessfulOrder(Order order) {
        log.info(
                "Order is completed. Instrument {}. price {}",
                order.getInstrumentName(),
                order.getPrice()
        );
    }

    @Override
    public void notifyFailedOrder(Order order) {
        log.info(
                "Order is failed. Instrument {}",
                order.getInstrumentName()
        );
    }
}
