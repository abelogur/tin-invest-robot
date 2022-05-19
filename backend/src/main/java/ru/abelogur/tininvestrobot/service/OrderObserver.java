package ru.abelogur.tininvestrobot.service;

import ru.abelogur.tininvestrobot.domain.Order;

public interface OrderObserver {
    void notifyNewOrder(Order order);

    void notifySuccessfulOrder(Order order);

    void notifyFailedOrder(Order order);
}
