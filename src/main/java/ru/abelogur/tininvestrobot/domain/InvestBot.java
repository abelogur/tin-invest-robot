package ru.abelogur.tininvestrobot.domain;

import lombok.Getter;
import ru.abelogur.tininvestrobot.dto.BotSettings;
import ru.abelogur.tininvestrobot.dto.CreateOrderInfo;
import ru.abelogur.tininvestrobot.helper.HelperUtils;
import ru.abelogur.tininvestrobot.service.OrderObserver;
import ru.abelogur.tininvestrobot.service.order.OrderService;
import ru.abelogur.tininvestrobot.strategy.InvestStrategy;

import java.math.BigDecimal;
import java.util.List;

public class InvestBot implements CandleObserver, OrderObserver {

    @Getter
    private final CandleGroupId groupId;
    @Getter
    private final BotSettings settings;
    private final List<CachedCandle> candles;
    @Getter
    private final InvestStrategy investStrategy;
    private final OrderService orderService;

    private Order order;

    public InvestBot(BotSettings settings, List<CachedCandle> candles,
                     InvestStrategy investStrategy, OrderService orderService) {
        var figi = settings.getFigi();
        var interval = HelperUtils.intervalFrom(investStrategy.getCode().getInterval());
        this.groupId = CandleGroupId.of(figi, interval);

        this.settings = settings;
        this.candles = candles;
        this.investStrategy = investStrategy;
        this.orderService = orderService;
    }

    @Override
    public void notifyCandle(CachedCandle candle) {
        candles.add(candle);
        investStrategy.setLastIndex(candles.size() - 1);

        cancelOrderIfNeed();

        checkForOpenLong();
        checkForOpenShort();
        checkForCloseLong();
        checkForCloseShort();
    }

    private void checkForOpenLong() {
        if (investStrategy.isLongSignal() && (order == null || order.isShort())) {
            if (order != null && order.isShort()) {
                orderService.closeShort(formOrderInfo(getLastCandle(), OrderReason.SIGNAL_LONG));
            }
            order = orderService.openLong(formOrderInfo(getLastCandle(), OrderReason.SIGNAL_LONG)).orElse(null);
        }
    }

    private void checkForOpenShort() {
        if (investStrategy.isShortSignal() && (order == null || order.isShort())) {
            if (order != null && order.isLong()) {
                orderService.closeLong(formOrderInfo(getLastCandle(), OrderReason.SIGNAL_SHORT));
            }
            order = orderService.openShort(formOrderInfo(getLastCandle(), OrderReason.SIGNAL_SHORT)).orElse(null);
        }
    }

    private void checkForCloseLong() {
        if (order == null || order.isShort()) {
            return;
        }
        var lastClosePrice = getLastCandle().getClosePrice();
        var takeProfitMultiplier = settings.getTakeProfit().add(BigDecimal.ONE);
        var stopLossMultiplier = BigDecimal.ONE.subtract(settings.getStopLoss());
        if (order.getPrice().multiply(takeProfitMultiplier).compareTo(lastClosePrice) <= 0) {
            orderService.closeLong(formOrderInfo(getLastCandle(), OrderReason.TAKE_PROFIT));
            order = null;
        } else if (order.getPrice().multiply(stopLossMultiplier).compareTo(lastClosePrice) >= 0) {
            orderService.closeLong(formOrderInfo(getLastCandle(), OrderReason.STOP_LOSS));
            order = null;
        }
    }

    private void checkForCloseShort() {
        if (order == null || order.isLong()) {
            return;
        }
        var lastClosePrice = getLastCandle().getClosePrice();
        var takeProfitMultiplier = BigDecimal.ONE.subtract(settings.getTakeProfit());
        var stopLossMultiplier = settings.getStopLoss().add(BigDecimal.ONE);
        if (order.getPrice().multiply(takeProfitMultiplier).compareTo(lastClosePrice) >= 0) {
            orderService.closeShort(formOrderInfo(getLastCandle(), OrderReason.TAKE_PROFIT));
            order = null;
        } else if (order.getPrice().multiply(stopLossMultiplier).compareTo(lastClosePrice) <= 0) {
            orderService.closeShort(formOrderInfo(getLastCandle(), OrderReason.STOP_LOSS));
            order = null;
        }
    }

    @Override
    public void notifyNewOrder(Order order) {
    }

    @Override
    public void notifySuccessfulOrder(Order order) {
        if (this.order != null && this.order.getId().equals(order.getId())) {
            this.order.updateOrder(order);
        }
    }

    @Override
    public void notifyFailedOrder(Order order) {
        if (this.order != null && this.order.getId().equals(order.getId())) {
            this.order = null;
        }
    }

    private CachedCandle getLastCandle() {
        return candles.get(candles.size() - 1);
    }

    private CreateOrderInfo formOrderInfo(CachedCandle candle, OrderReason reason) {
        return new CreateOrderInfo(settings.getUuid(), settings.getFigi(), reason, candle.getClosePrice(),
                candle.getTime(), settings.getAccountId(), settings.getNumberOfLots());
    }

    private void cancelOrderIfNeed() {
        if (order != null && order.isNew()) {
            orderService.cancelOrder(settings.getAccountId(), order.getId());
            order = null;
        }
    }
}
