package ru.abelogur.tininvestrobot.service.order;

import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.domain.OrderStatus;
import ru.abelogur.tininvestrobot.dto.CreateOrderInfo;
import ru.abelogur.tininvestrobot.repository.InstrumentRepository;
import ru.abelogur.tininvestrobot.repository.OrderHistoryRepository;
import ru.abelogur.tininvestrobot.service.SdkService;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class SandboxOrderService extends IntegrationOrderService {

    private final SdkService sdkService;
    private final OrderHistoryRepository orderHistoryRepository;

    public SandboxOrderService(SdkService sdkService, InstrumentRepository instrumentRepository,
                               OrderObserversHolder observersHolder, OrderHistoryRepository orderHistoryRepository) {
        super(instrumentRepository, observersHolder);
        this.sdkService = sdkService;
        this.orderHistoryRepository = orderHistoryRepository;
    }

    @Override
    public Optional<Order> openLong(CreateOrderInfo orderInfo) {
        Optional<Order> order = super.openLong(orderInfo);
        order.ifPresent(this::notifySuccessOrderObservers);
        return order;
    }

    @Override
    public Optional<Order> closeLong(CreateOrderInfo orderInfo) {
        Optional<Order> order = super.closeLong(orderInfo);
        order.ifPresent(this::notifySuccessOrderObservers);
        return order;
    }

    @Override
    public Optional<Order> openShort(CreateOrderInfo orderInfo) {
        Optional<Order> order = super.openShort(orderInfo);
        order.ifPresent(this::notifySuccessOrderObservers);
        return order;
    }

    @Override
    public Optional<Order> closeShort(CreateOrderInfo orderInfo) {
        Optional<Order> order = super.closeShort(orderInfo);
        order.ifPresent(this::notifySuccessOrderObservers);
        return order;
    }

    @Override
    protected PostOrderResponse buyMarket(CreateOrderInfo orderInfo) {
        var orderId = UUID.randomUUID().toString();
        return sdkService.getSandboxInvestApi().getSandboxService().postOrderSync(orderInfo.getFigi(), orderInfo.getNumberOfLots(),
                Quotation.getDefaultInstance(), OrderDirection.ORDER_DIRECTION_BUY, orderInfo.getAccountId(),
                OrderType.ORDER_TYPE_MARKET, orderId);
    }

    @Override
    protected PostOrderResponse sellMarket(CreateOrderInfo orderInfo) {
        var orderId = UUID.randomUUID().toString();
        return sdkService.getSandboxInvestApi().getSandboxService().postOrderSync(orderInfo.getFigi(), orderInfo.getNumberOfLots(),
                Quotation.getDefaultInstance(), OrderDirection.ORDER_DIRECTION_SELL, orderInfo.getAccountId(),
                OrderType.ORDER_TYPE_MARKET, orderId);
    }

    @Override
    public boolean cancelOrder(String accountId, String orderId) {
        Optional<Order> order = orderHistoryRepository.get(orderId);
        try {
            sdkService.getSandboxInvestApi().getSandboxService().cancelOrderSync(accountId, orderId);
            order.ifPresent(observersHolder::notifyFailedOrderObservers);
        } catch (Exception e) {
            return false;
        }
        return order.isPresent();
    }

    private void notifySuccessOrderObservers(Order order) {
        order.updateOrder(OrderStatus.SUCCESS, order.getPrice(), getCommission(order.getPrice()));
        observersHolder.notifySuccessOrderObservers(order);
    }

    private BigDecimal getCommission(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(0.003));
    }
}
