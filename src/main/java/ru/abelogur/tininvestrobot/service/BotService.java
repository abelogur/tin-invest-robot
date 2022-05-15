package ru.abelogur.tininvestrobot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.controller.dto.BotConfig;
import ru.abelogur.tininvestrobot.domain.CachedCandle;
import ru.abelogur.tininvestrobot.domain.CandleGroupId;
import ru.abelogur.tininvestrobot.domain.InvestBot;
import ru.abelogur.tininvestrobot.strategy.InvestStrategy;
import ru.abelogur.tininvestrobot.strategy.OneMinuteScalpingStrategy;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

@Service
@RequiredArgsConstructor
public class BotService {

    private final CandleService candleService;
    private final OrderService orderService;
    private final Simulator simulator;

    public void createBot(BotConfig config) {
        CandleGroupId groupId = CandleGroupId.of(config.getFigi(), CandleInterval.CANDLE_INTERVAL_1_MIN);
        SortedSet<CachedCandle> candles = candleService.loadHistoricCandles(groupId);
        List<CachedCandle> candleList = new ArrayList<>(candles);
        InvestStrategy investStrategy = new OneMinuteScalpingStrategy(candleList);
        InvestBot bot = new InvestBot(config.getFigi(), candleList, investStrategy, orderService,
                config.getTakeProfit(), config.getStopLoss());
        candleService.addObserver(groupId, bot);

    }

    public void createBotSimulation(BotConfig config, Instant startSimulation) {
        CandleGroupId groupId = CandleGroupId.of(config.getFigi(), CandleInterval.CANDLE_INTERVAL_1_MIN);
        SortedSet<CachedCandle> candles = candleService.loadHistoricCandles(groupId, startSimulation);
        List<CachedCandle> candleList = new ArrayList<>(candles);
        InvestStrategy investStrategy = new OneMinuteScalpingStrategy(candleList);
        InvestBot bot = new InvestBot(config.getFigi(), candleList, investStrategy, orderService,
                config.getTakeProfit(), config.getStopLoss());
        candleService.addObserver(groupId, bot);
        simulator.simulate(groupId, startSimulation);
        candleService.uncached(groupId);
        candleService.removeObserver(groupId, bot);
    }
}
