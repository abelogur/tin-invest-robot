package ru.abelogur.tininvestrobot.service;

import ru.abelogur.tininvestrobot.domain.Order;

import java.util.UUID;

public interface OrderObserver {
    void notifyOrder(UUID bootUuid, Order order);
}
