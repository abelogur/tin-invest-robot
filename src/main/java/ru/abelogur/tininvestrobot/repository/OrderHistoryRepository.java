package ru.abelogur.tininvestrobot.repository;

import ru.abelogur.tininvestrobot.domain.Order;

import java.util.SortedSet;
import java.util.UUID;

public interface OrderHistoryRepository {
    void add(UUID botUuid, Order order);

    SortedSet<Order> getAll(UUID botUuid);
}
