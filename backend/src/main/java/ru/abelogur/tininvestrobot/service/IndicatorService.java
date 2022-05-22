package ru.abelogur.tininvestrobot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.controller.exception.RestRuntimeException;
import ru.abelogur.tininvestrobot.dto.chart.Chart;
import ru.abelogur.tininvestrobot.dto.chart.ChartIndicators;
import ru.abelogur.tininvestrobot.dto.chart.ChartPoint;
import ru.abelogur.tininvestrobot.repository.InvestBotRepository;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndicatorService {

    private final InvestBotRepository investBotRepository;

    public Optional<Chart> getIndicators(UUID botUuid, Instant start, @Nullable Instant finish) {
        if (finish != null && start.isAfter(finish)) {
            throw new RestRuntimeException("Дата начала должна быть раньше даты конца", HttpStatus.BAD_REQUEST);
        }
        var bot = investBotRepository.get(botUuid)
                .orElseThrow(() -> new RestRuntimeException("Бот не найдет", HttpStatus.NOT_FOUND));
        var candles = bot.getCandles();
        if (candles.size() < 2) {
            return Optional.empty();
        }

        var startIndex = -1;
        var finishIndex = finish == null ? candles.size() - 1 : -1;
        for (int i = 0; i < candles.size(); i++) {
            if (startIndex == -1 && !candles.get(i).getTime().isBefore(start)) {
                startIndex = i;
            } else if (finish != null && finishIndex == -1 && candles.get(i).getTime().isAfter(finish)) {
                finishIndex = i - 1;
            }
            if (startIndex != -1 && finishIndex != -1) {
                break;
            }
        }
        if (startIndex == -1 || finishIndex == -1) {
            return Optional.empty();
        }

        var indicators = bot.getInvestStrategy().getValues(startIndex, finishIndex);
        var candlesOut = candles.subList(startIndex, finishIndex + 1).stream()
                .map(ChartPoint::of)
                .collect(Collectors.toList());
        var result = new HashMap<String, List<ChartPoint>>();
        indicators.keySet().forEach(key -> result.put(key, new ArrayList<>()));
        for (int i = 0; i < candlesOut.size(); i++) {
            var index = i;
            result.keySet().forEach(key -> result.get(key).add(
                    new ChartPoint(indicators.get(key).get(index), candlesOut.get(index).getTime()))
            );
        }

        return Optional.of(new Chart(candlesOut, result));
    }

    public ChartIndicators getLastIndicators(UUID botUuid) {
        var bot = investBotRepository.get(botUuid).orElse(null);
        if (bot == null) {
            return null;
        }
        var candles = bot.getCandles();
        var lastIndex = candles.size() - 1;
        var lastCandle = candles.get(lastIndex);
        var indicators = bot.getInvestStrategy().getValues(lastIndex, lastIndex);


        Map<String, ChartPoint> result = new HashMap<>();

        indicators.forEach(
                (indicator, values) -> result.put(indicator,
                        new ChartPoint(values.get(values.size() - 1), lastCandle.getTime()))
        );

        return new ChartIndicators(new ChartPoint(lastCandle.getClosePrice(), lastCandle.getTime()), result);
    }
}
