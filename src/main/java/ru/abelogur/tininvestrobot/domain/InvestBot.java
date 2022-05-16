package ru.abelogur.tininvestrobot.domain;

import ru.abelogur.tininvestrobot.dto.OrderMetadata;
import ru.abelogur.tininvestrobot.service.order.OrderService;
import ru.abelogur.tininvestrobot.strategy.InvestStrategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class InvestBot implements CandleObserver {

    private final UUID uuid;
    private final String figi;
    private final List<CachedCandle> candles;
    private final InvestStrategy investStrategy;
    private final OrderService orderService;
    private final BigDecimal takeProfit;
    private final BigDecimal stopLoss;

    private Order order;

    public InvestBot(UUID uuid, String figi, List<CachedCandle> candles, InvestStrategy investStrategy,
                     OrderService orderService, BigDecimal takeProfit, BigDecimal stopLoss) {
        this.uuid = uuid;
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
        if (investStrategy.isLongSignal() && (order == null || order.isShort())) {
            if (order != null && order.isShort()) {
                orderService.sellShort(figi, getOrderMetadata(getLastCandle(), OrderReason.SIGNAL_LONG));
            }
            order = orderService.buyLong(figi, getOrderMetadata(getLastCandle(), OrderReason.SIGNAL_LONG)).orElse(null);
        }
    }

    private void checkForOpenShort() {
        if (investStrategy.isShortSignal() && (order == null || order.isShort())) {
            if (order != null && order.isLong()) {
                orderService.sellLong(figi, getOrderMetadata(getLastCandle(), OrderReason.SIGNAL_SHORT));
            }
            order = orderService.buyShort(figi, getOrderMetadata(getLastCandle(), OrderReason.SIGNAL_SHORT)).orElse(null);
        }
    }

    private void checkForCloseLong() {
        if (order == null) {
            return;
        }
        var lastClosePrice = getLastCandle().getClosePrice();
        var takeProfitMultiplier = takeProfit.add(BigDecimal.ONE);
        var stopLossMultiplier = BigDecimal.ONE.subtract(stopLoss);
        if (order.getPrice().multiply(takeProfitMultiplier).compareTo(lastClosePrice) <= 0) {
            orderService.sellLong(figi, getOrderMetadata(getLastCandle(), OrderReason.TAKE_PROFIT));
            order = null;
        } else if (order.getPrice().multiply(stopLossMultiplier).compareTo(lastClosePrice) >= 0) {
            orderService.sellLong(figi, getOrderMetadata(getLastCandle(), OrderReason.STOP_LOSS));
            order = null;
        }
    }

    private void checkForCloseShort() {
        if (order == null) {
            return;
        }
        var lastClosePrice = getLastCandle().getClosePrice();
        var takeProfitMultiplier = BigDecimal.ONE.subtract(takeProfit);
        var stopLossMultiplier = stopLoss.add(BigDecimal.ONE);
        if (order.getPrice().multiply(takeProfitMultiplier).compareTo(lastClosePrice) >= 0) {
            orderService.sellShort(figi, getOrderMetadata(getLastCandle(), OrderReason.TAKE_PROFIT));
            order = null;
        } else if (order.getPrice().multiply(stopLossMultiplier).compareTo(lastClosePrice) <= 0) {
            orderService.sellShort(figi, getOrderMetadata(getLastCandle(), OrderReason.STOP_LOSS));
            order = null;
        }
    }

    private CachedCandle getLastCandle() {
        return candles.get(candles.size() - 1);
    }

    private OrderMetadata getOrderMetadata(CachedCandle candle, OrderReason reason) {
        return new OrderMetadata(uuid, reason, candle.getClosePrice(), candle.getTime());
    }
}
