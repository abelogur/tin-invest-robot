package ru.abelogur.tininvestrobot.service.order;

import org.springframework.stereotype.Component;
import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.dto.CreateOrderInfo;
import ru.tinkoff.piapi.core.exception.ApiRuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class OrderObserversHolder {

    private final List<OrderObserver> commonOrderObservers;
    private final Map<UUID, OrderObserver> specifiedObservers = new HashMap<>();

    public OrderObserversHolder(List<OrderObserver> orderObservers) {
        this.commonOrderObservers = orderObservers;
    }

    public void notifyNewOrderObservers(Order order) {
        commonOrderObservers.forEach(observer -> observer.notifyNewOrder(order));
        if (specifiedObservers.containsKey(order.getBotUuid())) {
            specifiedObservers.get(order.getBotUuid()).notifyNewOrder(order);
        }
    }

    public void notifySuccessOrderObservers(Order order) {
        commonOrderObservers.forEach(observer -> observer.notifySuccessfulOrder(order));
        if (specifiedObservers.containsKey(order.getBotUuid())) {
            specifiedObservers.get(order.getBotUuid()).notifySuccessfulOrder(order);
        }
    }

    public void notifyFailedOrderObservers(Order order) {
        commonOrderObservers.forEach(observer -> observer.notifyFailedOrder(order));
        if (specifiedObservers.containsKey(order.getBotUuid())) {
            specifiedObservers.get(order.getBotUuid()).notifyFailedOrder(order);
        }
    }

    public void notifyErrorObservers(CreateOrderInfo info, ApiRuntimeException e) {
        commonOrderObservers.forEach(observer -> observer.notifyError(info, e));
        if (specifiedObservers.containsKey(info.getBotUuid())) {
            specifiedObservers.get(info.getBotUuid()).notifyError(info, e);
        }
    }

    public void addSpecifiedObserver(UUID uuid, OrderObserver observer) {
        specifiedObservers.putIfAbsent(uuid, observer);
    }

    public void removeSpecifiedObserver(UUID uuid) {
        specifiedObservers.remove(uuid);
    }
}
