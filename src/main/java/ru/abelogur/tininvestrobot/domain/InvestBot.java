package ru.abelogur.tininvestrobot.domain;

import ru.abelogur.tininvestrobot.service.OrderService;
import ru.abelogur.tininvestrobot.strategy.InvestStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
        checkForOpenLong();
        checkForOpenShort();
        checkForCloseLong();
        checkForCloseShort();
    }

    private void checkForOpenLong() {
        if (investStrategy.isLongSignal()) {
            orderService.buyLong(figi).ifPresent(orders::add);
            orders.stream()
                    .filter(order -> order.isShort() && orderService.sellShort(figi))
                    .forEach(orders::remove);
        }
    }

    private void checkForOpenShort() {
        if (investStrategy.isShortSignal()) {
            orderService.buyShort(figi).ifPresent(orders::add);
            orders.stream()
                    .filter(order -> order.isLong() && orderService.sellLong(figi))
                    .forEach(orders::remove);
        }
    }

    private void checkForCloseLong() {
        var lastClosePrice = getLastCandle().getClosePrice();
        orders.forEach(order -> {
            var takeProfitMultiplier = takeProfit.add(BigDecimal.ONE);
            var stopLossMultiplier = BigDecimal.ONE.subtract(stopLoss);
            if (order.getOpenPrice().multiply(takeProfitMultiplier).compareTo(lastClosePrice) >= 0) {
                orderService.sellLong(figi);
            } else if (order.getOpenPrice().multiply(stopLossMultiplier).compareTo(lastClosePrice) <= 0) {
                orderService.sellLong(figi);
            }
        });
    }

    private void checkForCloseShort() {
        var lastClosePrice = getLastCandle().getClosePrice();
        orders.forEach(order -> {
            var takeProfitMultiplier = BigDecimal.ONE.subtract(takeProfit);
            var stopLossMultiplier = stopLoss.add(BigDecimal.ONE);
            if (order.getOpenPrice().multiply(takeProfitMultiplier).compareTo(lastClosePrice) >= 0) {
                orderService.sellShort(figi);
            } else if (order.getOpenPrice().multiply(stopLossMultiplier).compareTo(lastClosePrice) <= 0) {
                orderService.sellShort(figi);
            }
        });
    }

    private CachedCandle getLastCandle() {
        return candles.get(candles.size() - 1);
    }
}
