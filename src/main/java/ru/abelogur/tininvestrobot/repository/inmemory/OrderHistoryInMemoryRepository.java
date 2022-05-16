package ru.abelogur.tininvestrobot.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.repository.OrderHistoryRepository;
import ru.abelogur.tininvestrobot.service.OrderObserver;

import java.util.*;
import java.util.function.Supplier;

@Repository
public class OrderHistoryInMemoryRepository implements OrderHistoryRepository, OrderObserver {

    private final HashMap<UUID, TreeSet<Order>> orders = new HashMap<>();

    private static final Supplier<TreeSet<Order>> EMPTY_TREE_SET = () -> new TreeSet<>(
            Comparator.comparing(Order::getTime)
    );


    @Override
    public void add(UUID botUuid, Order order) {
        orders.putIfAbsent(botUuid, EMPTY_TREE_SET.get());
        orders.get(botUuid).add(order);
    }

    @Override
    public SortedSet<Order> getAll(UUID botUuid) {
        return orders.getOrDefault(botUuid, EMPTY_TREE_SET.get());
    }

    @Override
    public void notifyOrder(UUID botUuid, Order order) {
        add(botUuid, order);
    }
}
