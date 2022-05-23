package ru.abelogur.tininvestrobot.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.repository.OrderHistoryRepository;
import ru.abelogur.tininvestrobot.service.order.OrderObserver;

import java.util.*;
import java.util.function.Supplier;

@Repository
public class OrderHistoryInMemoryRepository implements OrderHistoryRepository, OrderObserver {

    private final HashMap<UUID, TreeSet<Order>> orders = new HashMap<>();

    private static final Supplier<TreeSet<Order>> EMPTY_TREE_SET = () -> new TreeSet<>(
            Comparator.comparing(Order::getTime)
    );

    @Override
    public Optional<Order> get(String orderId) {
        return orders.values().stream()
                .flatMap(Collection::stream)
                .filter(order -> order.getId().equals(orderId))
                .findFirst();
    }

    @Override
    public void add(UUID botUuid, Order order) {
        orders.putIfAbsent(botUuid, EMPTY_TREE_SET.get());
        orders.get(botUuid).add(order);
    }

    @Override
    public void update(Order order) {
        get(order.getId())
                .ifPresent(it -> it.updateOrder(order));
    }

    @Override
    public SortedSet<Order> getAll(UUID botUuid) {
        return orders.getOrDefault(botUuid, EMPTY_TREE_SET.get());
    }

    @Override
    public void remove(UUID botUuid, String orderId) {
        if(orders.containsKey(botUuid)) {
            orders.get(botUuid).removeIf(order -> order.getId().equals(orderId));
        }
    }

    @Override
    public void notifyNewOrder(Order order) {
        add(order.getBotUuid(), order);
    }

    @Override
    public void notifySuccessfulOrder(Order order) {
        add(order.getBotUuid(), order);
    }

    @Override
    public void notifyFailedOrder(Order order) {
        remove(order.getBotUuid(), order.getId());
    }
}
