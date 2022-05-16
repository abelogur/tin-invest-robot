package ru.abelogur.tininvestrobot.dto;

import lombok.Getter;
import ru.abelogur.tininvestrobot.domain.Order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class StatisticDto {
    private List<OrderDto> orders;
    private BigDecimal profit;
    private BigDecimal profitPercentage;
    private BigDecimal commission;
    private BigDecimal profitWithCommission;
    private BigDecimal profitWithCommissionPercentage;
    private BigDecimal usedMoney;

    public StatisticDto(Collection<Order> orders, BigDecimal profit, BigDecimal commission, BigDecimal usedMoney) {
        this.orders = orders.stream()
                .map(OrderDto::map)
                .collect(Collectors.toList());
        this.profit = profit;
        this.profitPercentage = usedMoney.equals(BigDecimal.ZERO) ? BigDecimal.ZERO
                : profit.multiply(BigDecimal.valueOf(100)).divide(usedMoney, 2, RoundingMode.HALF_UP);
        this.commission = commission;
        this.profitWithCommission = profit.subtract(commission);
        this.profitWithCommissionPercentage = usedMoney.add(commission).equals(BigDecimal.ZERO) ? BigDecimal.ZERO
                : profitWithCommission.multiply(BigDecimal.valueOf(100)).divide(usedMoney.add(commission), 2, RoundingMode.HALF_UP);
        this.usedMoney = usedMoney;
    }

    public static StatisticDto empty() {
        return new StatisticDto(Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
