package ru.abelogur.tininvestrobot.domain;

import ru.abelogur.tininvestrobot.dto.OrderMetadata;
import ru.abelogur.tininvestrobot.service.OrderService;
import ru.abelogur.tininvestrobot.strategy.InvestStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InvestBot implements CandleObserver {

    private final String figi;
    private final List<CachedCandle> candles;
    private final InvestStrategy investStrategy;
    private final OrderService orderService;
    private final BigDecimal takeProfit;
    private final BigDecimal stopLoss;

    private final List<Order> orders = new ArrayList<>();

    public InvestBot(String figi, List<CachedCandle> candles, InvestStrategy investStrategy,
                     OrderService orderService, BigDecimal takeProfit, BigDecimal stopLoss) {
        this.figi = figi;
        this.candles = candles;
        this.investStrategy = investStrategy;
        this.orderService = orderService;
        this.takeProfit = takeProfit;
        this.stopLoss = stopLoss;
    }

    @Override
    public void notifyCandle(CachedCandle candle) {
        candles.add(candle);
        investStrategy.setLastIndex(candles.size() - 1);
        checkForOpenLong();
        checkForOpenShort();
        checkForCloseLong();
        checkForCloseShort();
    }

    private void checkForOpenLong() {
        if (investStrategy.isLongSignal()) {
            orderService.buyLong(figi, getOrderMetadata(getLastCandle(), "Signal")).ifPresent(orders::add);
            orders.removeAll(orders.stream()
                    .filter(order -> order.isShort() && orderService.sellShort(figi, getOrderMetadata(getLastCandle(), "Buy long")))
                    .collect(Collectors.toList()));
        }
    }

    private void checkForOpenShort() {
        if (investStrategy.isShortSignal()) {
            orderService.buyShort(figi, getOrderMetadata(getLastCandle(), "Signal")).ifPresent(orders::add);
            orders.removeAll(orders.stream()
                    .filter(order -> order.isLong() && orderService.sellLong(figi, getOrderMetadata(getLastCandle(), "Buy short")))
                    .collect(Collectors.toList()));
        }
    }

    private void checkForCloseLong() {
        var lastClosePrice = getLastCandle().getClosePrice();
        List<Order> ordersToSell = orders.stream()
                .filter(Order::isLong)
                .filter(order -> {
                    var takeProfitMultiplier = takeProfit.add(BigDecimal.ONE);
                    var stopLossMultiplier = BigDecimal.ONE.subtract(stopLoss);
                    if (order.getOpenPrice().multiply(takeProfitMultiplier).compareTo(lastClosePrice) <= 0) {
                        return orderService.sellLong(figi, getOrderMetadata(getLastCandle(), "Take profit"));
                    } else if (order.getOpenPrice().multiply(stopLossMultiplier).compareTo(lastClosePrice) >= 0) {
                        return orderService.sellLong(figi, getOrderMetadata(getLastCandle(), "Stop loss"));
                    } else {
                        return false;
                    }
                }).collect(Collectors.toList());
        orders.removeAll(ordersToSell);
    }

    private void checkForCloseShort() {
        var lastClosePrice = getLastCandle().getClosePrice();
        List<Order> ordersToSell = orders.stream()
                .filter(Order::isShort)
                .filter(order -> {
                    var takeProfitMultiplier = BigDecimal.ONE.subtract(takeProfit);
                    var stopLossMultiplier = stopLoss.add(BigDecimal.ONE);
                    if (order.getOpenPrice().multiply(takeProfitMultiplier).compareTo(lastClosePrice) >= 0) {
                        return orderService.sellShort(figi, getOrderMetadata(getLastCandle(), "Take profit"));
                    } else if (order.getOpenPrice().multiply(stopLossMultiplier).compareTo(lastClosePrice) <= 0) {
                        return orderService.sellShort(figi, getOrderMetadata(getLastCandle(), "Stop loss"));
                    } else {
                        return false;
                    }
                }).collect(Collectors.toList());
        orders.removeAll(ordersToSell);
    }

    private CachedCandle getLastCandle() {
        return candles.get(candles.size() - 1);
    }

    private OrderMetadata getOrderMetadata(CachedCandle candle, String reason) {
        return new OrderMetadata(reason, candle.getClosePrice(), candle.getTime());
    }
}
