package ru.abelogur.tininvestrobot.dto;

import lombok.Getter;
import ru.abelogur.tininvestrobot.domain.Order;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.math.RoundingMode.HALF_UP;

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
                : profit.multiply(BigDecimal.valueOf(100)).divide(usedMoney, 2, HALF_UP);
        this.commission = commission;
        this.profitWithCommission = profit.subtract(commission);
        this.profitWithCommissionPercentage = usedMoney.add(commission).equals(BigDecimal.ZERO) ? BigDecimal.ZERO
                : profitWithCommission.multiply(BigDecimal.valueOf(100)).divide(usedMoney.add(commission), 2, HALF_UP);
        this.usedMoney = usedMoney;
    }

    public StatisticDto multiply(BigDecimal multiplier) {
        profit = profit.multiply(multiplier).setScale(2, HALF_UP);
        commission = commission.multiply(multiplier).setScale(2, HALF_UP);
        profitWithCommission = profitWithCommission.multiply(multiplier).setScale(2, HALF_UP);
        usedMoney = usedMoney.multiply(multiplier).setScale(2, HALF_UP);
        return this;
    }

    public StatisticDto sum(StatisticDto other) {
        orders = Collections.emptyList();
        profit = profit.add(other.getProfit());
        commission = commission.add(other.getCommission());
        profitWithCommission = profitWithCommission.add(other.getProfitWithCommission());
        usedMoney = usedMoney.add(other.getUsedMoney());
        profitPercentage = usedMoney.equals(BigDecimal.ZERO) ? BigDecimal.ZERO
                : profit.multiply(BigDecimal.valueOf(100)).divide(usedMoney, 2, HALF_UP);
        this.profitWithCommissionPercentage = usedMoney.add(commission).equals(BigDecimal.ZERO) ? BigDecimal.ZERO
                : profitWithCommission.multiply(BigDecimal.valueOf(100)).divide(usedMoney.add(commission), 2, HALF_UP);
        return this;
    }

    public static StatisticDto empty() {
        return new StatisticDto(Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
