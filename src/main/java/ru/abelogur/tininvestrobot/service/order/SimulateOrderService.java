package ru.abelogur.tininvestrobot.service.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.domain.OrderAction;
import ru.abelogur.tininvestrobot.domain.TradeType;
import ru.abelogur.tininvestrobot.dto.OrderMetadata;
import ru.abelogur.tininvestrobot.repository.InstrumentRepository;
import ru.abelogur.tininvestrobot.service.OrderObserver;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulateOrderService implements OrderService {

    private final List<OrderObserver> orderObservers;
    private final InstrumentRepository instrumentRepository;

    public Optional<Order> buyLong(String figi, OrderMetadata metadata) {
        Order order = Order.of(TradeType.LONG, OrderAction.BUY, getCommission(metadata.getPrice()), metadata, instrumentRepository.get(figi));
        notifyObservers(metadata.getBotUuid(), order);
        return Optional.of(order);
    }

    public boolean sellLong(String figi, OrderMetadata metadata) {
        Order order = Order.of(TradeType.LONG, OrderAction.SELL, getCommission(metadata.getPrice()), metadata, instrumentRepository.get(figi));
        notifyObservers(metadata.getBotUuid(), order);
        return true;
    }

    public Optional<Order> buyShort(String figi, OrderMetadata metadata) {
//        return Optional.empty();
        Order order = Order.of(TradeType.SHORT, OrderAction.BUY, getCommission(metadata.getPrice()), metadata, instrumentRepository.get(figi));
        notifyObservers(metadata.getBotUuid(), order);
        return Optional.of(order);
    }

    public boolean sellShort(String figi, OrderMetadata metadata) {
        Order order = Order.of(TradeType.SHORT, OrderAction.SELL, getCommission(metadata.getPrice()), metadata, instrumentRepository.get(figi));
        notifyObservers(metadata.getBotUuid(), order);
        return true;
    }

    private void notifyObservers(UUID botUuid, Order order) {
        orderObservers.forEach(it -> it.notifyOrder(botUuid, order));
    }

    private BigDecimal getCommission(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(0.003));
    }
}
