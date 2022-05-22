package ru.abelogur.tininvestrobot.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.dto.CreateOrderInfo;
import ru.abelogur.tininvestrobot.repository.InvestBotRepository;
import ru.abelogur.tininvestrobot.service.order.OrderObserver;
import ru.tinkoff.piapi.core.exception.ApiRuntimeException;

/**
 * Логирование заявок
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventLogger implements OrderObserver {

    private final InvestBotRepository botRepository;

    @Override
    public void notifyNewOrder(Order order) {
        log.info(
                "Order {} is opened on {}. Instrument {}. {} {} by reason {} at {}. price {}",
                order.getId(),
                botRepository.get(order.getBotUuid()).map(it -> it.getState().getBotEnv()).orElse(null),
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
                "Order is completed {}. Instrument {}. price {}",
                order.getId(),
                order.getInstrumentName(),
                order.getPrice()
        );
    }

    @Override
    public void notifyFailedOrder(Order order) {
        log.info(
                "Order {} is failed. Instrument {}",
                order.getId(),
                order.getInstrumentName()
        );
    }

    @Override
    public void notifyError(CreateOrderInfo info, ApiRuntimeException e) {
        log.info(
                "Cannot start order on {}. Order reason is {}. Error message is {}",
                botRepository.get(info.getBotUuid()).map(it -> it.getState().getBotEnv()).orElse(null),
                info.getReason(),
                e.getMessage()
        );
    }
}
