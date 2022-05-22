package ru.abelogur.tininvestrobot.service.order;

import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.dto.CreateOrderInfo;
import ru.tinkoff.piapi.core.exception.ApiRuntimeException;

public interface OrderObserver {
    default void notifyNewOrder(Order order) {}

    default void notifySuccessfulOrder(Order order) {}

    default void notifyFailedOrder(Order order) {}

    default void notifyError(CreateOrderInfo info, ApiRuntimeException e) {}
}
