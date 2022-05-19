package ru.abelogur.tininvestrobot.service.order;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.domain.OrderStatus;
import ru.abelogur.tininvestrobot.dto.CreateOrderInfo;
import ru.abelogur.tininvestrobot.helper.OrderObserversHolder;
import ru.abelogur.tininvestrobot.repository.InstrumentRepository;
import ru.abelogur.tininvestrobot.repository.OrderHistoryRepository;
import ru.abelogur.tininvestrobot.service.SdkService;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.stream.StreamProcessor;
import ru.tinkoff.piapi.core.utils.MapperUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RealOrderService extends IntegrationOrderService {

    private final SdkService sdkService;
    private final OrderHistoryRepository orderHistoryRepository;

    public RealOrderService(SdkService sdkService, InstrumentRepository instrumentRepository,
                            OrderObserversHolder observersHolder, OrderHistoryRepository orderHistoryRepository) {
        super(instrumentRepository, observersHolder);
        this.sdkService = sdkService;
        this.orderHistoryRepository = orderHistoryRepository;

        runOrdersStream();
    }

    @Override
    protected PostOrderResponse buyMarket(CreateOrderInfo orderInfo) {
        var orderId = UUID.randomUUID().toString();
        return sdkService.getInvestApi().getOrdersService().postOrderSync(orderInfo.getFigi(), orderInfo.getNumberOfLots(),
                Quotation.getDefaultInstance(), OrderDirection.ORDER_DIRECTION_BUY, orderInfo.getAccountId(),
                OrderType.ORDER_TYPE_MARKET, orderId);
    }

    @Override
    protected PostOrderResponse sellMarket(CreateOrderInfo orderInfo) {
        var orderId = UUID.randomUUID().toString();
        return sdkService.getInvestApi().getOrdersService().postOrderSync(orderInfo.getFigi(), orderInfo.getNumberOfLots(),
                Quotation.getDefaultInstance(), OrderDirection.ORDER_DIRECTION_BUY, orderInfo.getAccountId(),
                OrderType.ORDER_TYPE_MARKET, orderId);
    }

    @Override
    public boolean cancelOrder(String accountId, String orderId) {
        Optional<Order> order = orderHistoryRepository.get(orderId);
        try {
            sdkService.getInvestApi().getOrdersService().cancelOrderSync(accountId, orderId);
            order.ifPresent(observersHolder::notifyFailedOrderObservers);
        } catch (Exception e) {
            return false;
        }
        return order.isPresent();
    }

    private void runOrdersStream() {
        var accounts = sdkService.getInvestApi().getUserService().getAccountsSync()
                .stream().map(Account::getId)
                .collect(Collectors.toList());

        StreamProcessor<TradesStreamResponse> consumer = response -> {
            if (response.hasPing()) {
                log.info("Trades stream ping");
            } else if (response.hasOrderTrades()) {
                log.info("Новые данные по сделкам: {}", response);
                updateOrder(response.getOrderTrades());
            }
        };

        Consumer<Throwable> onErrorCallback = error -> reconnect(consumer, accounts).accept(error);

        sdkService.getInvestApi().getOrdersStreamService().subscribeTrades(consumer, onErrorCallback, accounts);
    }

    private void updateOrder(OrderTrades trades) {
        OrderState state = sdkService.getInvestApi().getOrdersService()
                .getOrderStateSync(trades.getAccountId(), trades.getOrderId());
        orderHistoryRepository.get(state.getOrderId())
                .ifPresent(order -> {
                    order.updateOrder(
                            OrderStatus.from(state.getExecutionReportStatus()),
                            MapperUtils.moneyValueToBigDecimal(state.getExecutedOrderPrice()),
                            MapperUtils.moneyValueToBigDecimal(state.getExecutedCommission())
                    );
                    orderHistoryRepository.update(order);
                    observersHolder.notifySuccessOrderObservers(order);
                });
    }

    private Consumer<Throwable> reconnect(StreamProcessor<TradesStreamResponse> consumer, List<String> accounts) {
        return error -> {
            log.error(error.toString());
            if (error instanceof StatusRuntimeException
                    && (((StatusRuntimeException) error).getStatus().getCode().equals(Status.Code.UNAVAILABLE)
                    || ((StatusRuntimeException) error).getStatus().getCode().equals(Status.Code.INTERNAL))) {
                delay(1000);
                log.info("Reconnect trade stream");
                sdkService.getInvestApi().getOrdersStreamService()
                        .subscribeTrades(consumer, reconnect(consumer, accounts), accounts);
            }
        };
    }

    @SneakyThrows
    private void delay(long millis) {
        Thread.sleep(millis);
    }
}
