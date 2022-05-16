package ru.abelogur.tininvestrobot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.domain.OrderAction;
import ru.abelogur.tininvestrobot.domain.TradeType;
import ru.abelogur.tininvestrobot.dto.StatisticDto;
import ru.abelogur.tininvestrobot.repository.InstrumentRepository;
import ru.abelogur.tininvestrobot.repository.OrderHistoryRepository;
import ru.tinkoff.piapi.core.utils.MapperUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final OrderHistoryRepository orderHistoryRepository;
    private final SdkService sdkService;
    private final InstrumentRepository instrumentRepository;

    public StatisticDto getStatistic(UUID botUuid) {
        var orders = orderHistoryRepository.getAll(botUuid);
        if (orders.isEmpty()) {
            return StatisticDto.empty();
        }

        var profit = BigDecimal.ZERO;
        var commission = BigDecimal.ZERO;
        var usedMoney = BigDecimal.ZERO;
        var longs = 0;
        var shorts = 0;

        for (var order : orders) {
            if (order.getAction().equals(OrderAction.BUY) && order.getType().equals(TradeType.LONG)) {
                longs++;
                profit = profit.subtract(order.getPrice());
            } else if (order.getAction().equals(OrderAction.SELL) && order.getType().equals(TradeType.SHORT)) {
                shorts--;
                profit = profit.subtract(order.getPrice());
            } else if (order.getAction().equals(OrderAction.SELL) && order.getType().equals(TradeType.LONG)) {
                longs--;
                profit = profit.add(order.getPrice());
            } else if (order.getAction().equals(OrderAction.BUY) && order.getType().equals(TradeType.SHORT)) {
                shorts++;
                profit = profit.add(order.getPrice());
            }
            commission = commission.add(order.getCommission());
            if (profit.compareTo(usedMoney.negate()) < 0) {
                usedMoney = profit.negate();
            }
        }

        var figi = orders.last().getInstrument().getFigi();
        var lastPrice = getLastPrice(figi);
        profit = profit.add(lastPrice.multiply(BigDecimal.valueOf(longs)));
        profit = profit.subtract(lastPrice.multiply(BigDecimal.valueOf(shorts)));
        return new StatisticDto(orders, profit, commission, usedMoney);
    }

    private BigDecimal getLastPrice(String figi) {
        var price = sdkService.getInvestApi().getMarketDataService().getLastPricesSync(List.of(figi)).get(0);
        return MapperUtils.quotationToBigDecimal(price.getPrice()).multiply(instrumentRepository.get(figi).getLot());
    }
}
