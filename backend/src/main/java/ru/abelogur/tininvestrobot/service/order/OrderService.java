package ru.abelogur.tininvestrobot.service.order;

import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.dto.CreateOrderInfo;

import java.util.Optional;

/**
 * Сервис работы с поручениями. Одна реализация на каждое окружение
 *
 * "боевое" окружение - {@link RealOrderService}
 * песочница - {@link SandboxOrderService}
 * симуляция - {@link SimulateOrderService}
 *
 * Абстракция общих признаков для окружений, которые интегрируются с API Тинькофф Инвестиции - {@link IntegrationOrderService}
 *
 */
public interface OrderService {
    Optional<Order> openLong(CreateOrderInfo info);

    Optional<Order> closeLong(CreateOrderInfo info);

    Optional<Order> openShort(CreateOrderInfo info);

    Optional<Order> closeShort(CreateOrderInfo info);

    boolean cancelOrder(String accountId, String orderId);
}
