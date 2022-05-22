package ru.abelogur.tininvestrobot.service.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.domain.OrderAction;
import ru.abelogur.tininvestrobot.domain.OrderStatus;
import ru.abelogur.tininvestrobot.domain.TradeType;
import ru.abelogur.tininvestrobot.dto.CreateOrderInfo;
import ru.abelogur.tininvestrobot.repository.InstrumentRepository;
import ru.abelogur.tininvestrobot.repository.OrderHistoryRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulateOrderService implements OrderService {

    private final OrderObserversHolder observersHolder;
    private final InstrumentRepository instrumentRepository;
    private final OrderHistoryRepository orderHistoryRepository;

    @Override
    public Optional<Order> openLong(CreateOrderInfo orderInfo) {
        var order = new Order(
                UUID.randomUUID().toString(),
                orderInfo.getBotUuid(),
                TradeType.LONG,
                orderInfo.getPrice(),
                orderInfo.getNumberOfLots(),
                getCommission(orderInfo.getPrice()),
                orderInfo.getTime(),
                orderInfo.getReason(),
                OrderAction.OPEN,
                OrderStatus.SUCCESS,
                instrumentRepository.get(orderInfo.getFigi()).getName()
        );
        observersHolder.notifyNewOrderObservers(order);
        observersHolder.notifySuccessOrderObservers(order);
        return Optional.of(order);
    }

    @Override
    public Optional<Order> closeLong(CreateOrderInfo orderInfo) {
        var order = new Order(
                UUID.randomUUID().toString(),
                orderInfo.getBotUuid(),
                TradeType.LONG,
                orderInfo.getPrice(),
                orderInfo.getNumberOfLots(),
                getCommission(orderInfo.getPrice()),
                orderInfo.getTime(),
                orderInfo.getReason(),
                OrderAction.CLOSE,
                OrderStatus.SUCCESS,
                instrumentRepository.get(orderInfo.getFigi()).getName()
        );
        observersHolder.notifyNewOrderObservers(order);
        observersHolder.notifySuccessOrderObservers(order);
        return Optional.of(order);
    }

    @Override
    public Optional<Order> openShort(CreateOrderInfo orderInfo) {
        var order = new Order(
                UUID.randomUUID().toString(),
                orderInfo.getBotUuid(),
                TradeType.SHORT,
                orderInfo.getPrice(),
                orderInfo.getNumberOfLots(),
                getCommission(orderInfo.getPrice()),
                orderInfo.getTime(),
                orderInfo.getReason(),
                OrderAction.OPEN,
                OrderStatus.SUCCESS,
                instrumentRepository.get(orderInfo.getFigi()).getName()
        );
        observersHolder.notifyNewOrderObservers(order);
        observersHolder.notifySuccessOrderObservers(order);
        return Optional.of(order);
    }

    @Override
    public Optional<Order> closeShort(CreateOrderInfo orderInfo) {
        var order = new Order(
                UUID.randomUUID().toString(),
                orderInfo.getBotUuid(),
                TradeType.SHORT,
                orderInfo.getPrice(),
                orderInfo.getNumberOfLots(),
                getCommission(orderInfo.getPrice()),
                orderInfo.getTime(),
                orderInfo.getReason(),
                OrderAction.CLOSE,
                OrderStatus.SUCCESS,
                instrumentRepository.get(orderInfo.getFigi()).getName()
        );
        observersHolder.notifyNewOrderObservers(order);
        observersHolder.notifySuccessOrderObservers(order);
        return Optional.of(order);
    }

    @Override
    public boolean cancelOrder(String accountId, String orderId) {
        Optional<Order> order = orderHistoryRepository.get(orderId);
        order.ifPresent(observersHolder::notifyFailedOrderObservers);
        return order.isPresent();
    }

    private BigDecimal getCommission(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(0.003));
    }
}
