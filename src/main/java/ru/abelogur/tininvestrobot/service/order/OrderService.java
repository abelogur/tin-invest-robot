package ru.abelogur.tininvestrobot.service.order;

import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.dto.OrderMetadata;

import java.util.Optional;

public interface OrderService {

    Optional<Order> buyLong(String figi, OrderMetadata metadata);

    boolean sellLong(String figi, OrderMetadata metadata);

    Optional<Order> buyShort(String figi, OrderMetadata metadata);

    boolean sellShort(String figi, OrderMetadata metadata);
}
