package ru.abelogur.tininvestrobot.service.order;

import lombok.RequiredArgsConstructor;
import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.domain.OrderAction;
import ru.abelogur.tininvestrobot.domain.OrderStatus;
import ru.abelogur.tininvestrobot.domain.TradeType;
import ru.abelogur.tininvestrobot.dto.CreateOrderInfo;
import ru.abelogur.tininvestrobot.helper.OrderObserversHolder;
import ru.abelogur.tininvestrobot.repository.InstrumentRepository;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;
import ru.tinkoff.piapi.core.exception.ApiRuntimeException;
import ru.tinkoff.piapi.core.utils.MapperUtils;

import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class IntegrationOrderService implements OrderService {

    protected final InstrumentRepository instrumentRepository;
    protected final OrderObserversHolder observersHolder;

    public Optional<Order> openLong(CreateOrderInfo orderInfo) {
        try {
            var response = buyMarket(orderInfo);
            var order = new Order(
                    response.getOrderId(),
                    orderInfo.getBotUuid(),
                    TradeType.LONG,
                    MapperUtils.moneyValueToBigDecimal(response.getTotalOrderAmount()),
                    orderInfo.getNumberOfLots(),
                    BigDecimal.ZERO,
                    orderInfo.getTime(),
                    orderInfo.getReason(),
                    OrderAction.OPEN,
                    OrderStatus.from(response.getExecutionReportStatus()),
                    instrumentRepository.get(orderInfo.getFigi()).getName()
            );
            observersHolder.notifyNewOrderObservers(order);
            return Optional.of(order);
        } catch (ApiRuntimeException e) {
            observersHolder.notifyErrorObservers(orderInfo, e);
            return Optional.empty();
        }
    }

    public Optional<Order> closeLong(CreateOrderInfo orderInfo) {
        try {
            var response = sellMarket(orderInfo);
            var order = new Order(
                    response.getOrderId(),
                    orderInfo.getBotUuid(),
                    TradeType.LONG,
                    MapperUtils.moneyValueToBigDecimal(response.getTotalOrderAmount()),
                    orderInfo.getNumberOfLots(),
                    BigDecimal.ZERO,
                    orderInfo.getTime(),
                    orderInfo.getReason(),
                    OrderAction.CLOSE,
                    OrderStatus.from(response.getExecutionReportStatus()),
                    instrumentRepository.get(orderInfo.getFigi()).getName()
            );
            observersHolder.notifyNewOrderObservers(order);
            return Optional.of(order);
        } catch (ApiRuntimeException e) {
            observersHolder.notifyErrorObservers(orderInfo, e);
            return Optional.empty();
        }
    }

    public Optional<Order> openShort(CreateOrderInfo orderInfo) {
        try {
            var response = sellMarket(orderInfo);
            var order = new Order(
                    response.getOrderId(),
                    orderInfo.getBotUuid(),
                    TradeType.SHORT,
                    MapperUtils.moneyValueToBigDecimal(response.getTotalOrderAmount()),
                    orderInfo.getNumberOfLots(),
                    BigDecimal.ZERO,
                    orderInfo.getTime(),
                    orderInfo.getReason(),
                    OrderAction.OPEN,
                    OrderStatus.from(response.getExecutionReportStatus()),
                    instrumentRepository.get(orderInfo.getFigi()).getName()
            );
            observersHolder.notifyNewOrderObservers(order);
            return Optional.of(order);
        } catch (ApiRuntimeException e) {
            observersHolder.notifyErrorObservers(orderInfo, e);
            return Optional.empty();
        }
    }

    public Optional<Order> closeShort(CreateOrderInfo orderInfo) {
        try {
            var response = buyMarket(orderInfo);
            var order = new Order(
                    response.getOrderId(),
                    orderInfo.getBotUuid(),
                    TradeType.SHORT,
                    MapperUtils.moneyValueToBigDecimal(response.getTotalOrderAmount()),
                    orderInfo.getNumberOfLots(),
                    BigDecimal.ZERO,
                    orderInfo.getTime(),
                    orderInfo.getReason(),
                    OrderAction.CLOSE,
                    OrderStatus.from(response.getExecutionReportStatus()),
                    instrumentRepository.get(orderInfo.getFigi()).getName()
            );
            observersHolder.notifyNewOrderObservers(order);
            return Optional.of(order);
        } catch (ApiRuntimeException e) {
            observersHolder.notifyErrorObservers(orderInfo, e);
            return Optional.empty();
        }
    }

    protected abstract PostOrderResponse buyMarket(CreateOrderInfo orderInfo);

    protected abstract PostOrderResponse sellMarket(CreateOrderInfo orderInfo);
}
