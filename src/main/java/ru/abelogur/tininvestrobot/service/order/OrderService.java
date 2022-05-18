package ru.abelogur.tininvestrobot.service.order;

import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.dto.CreateOrderInfo;

import java.util.Optional;

public interface OrderService {

    Optional<Order> openLong(CreateOrderInfo metadata);

    Optional<Order> closeLong(CreateOrderInfo metadata);

    Optional<Order> openShort(CreateOrderInfo metadata);

    Optional<Order> closeShort(CreateOrderInfo metadata);

    boolean cancelOrder(String accountId, String orderId);
}
