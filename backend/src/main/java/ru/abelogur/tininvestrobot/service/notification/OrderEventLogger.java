package ru.abelogur.tininvestrobot.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.dto.CreateOrderInfo;
import ru.abelogur.tininvestrobot.service.order.OrderObserver;
import ru.tinkoff.piapi.core.exception.ApiRuntimeException;

@Slf4j
@Component
public class OrderEventLogger implements OrderObserver {

    @Override
    public void notifyNewOrder(Order order) {
        log.info(
                "Order is opened. Instrument {}. {} {} by reason {} at {}. price {}",
                order.getInstrumentName(),
                order.getAction().name(),
                order.getType().name(),
                order.getReason().name(),
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

    @Override
    public void notifyError(CreateOrderInfo info, ApiRuntimeException e) {
        log.info(
                "Cannot start order. Order reason is {}. Error message is {}",
                info.getReason(),
                e.getMessage()
        );
    }
}
