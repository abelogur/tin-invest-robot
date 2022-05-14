package ru.abelogur.tininvestrobot.service;

import ru.abelogur.tininvestrobot.domain.Order;

import java.util.Optional;

public class OrderService {

    public Optional<Order> buyLong(String figi) {
        return Optional.empty();
    }

    public boolean sellLong(String figi) {
        return false;
    }

    public Optional<Order> buyShort(String figi) {
        return Optional.empty();
    }

    public boolean sellShort(String figi) {
        return false;
    }
}
