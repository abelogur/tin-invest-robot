package ru.abelogur.tininvestrobot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.domain.CandleGroupId;
import ru.abelogur.tininvestrobot.domain.InvestBot;
import ru.abelogur.tininvestrobot.dto.BotConfig;
import ru.abelogur.tininvestrobot.service.order.RealOrderService;
import ru.abelogur.tininvestrobot.service.order.SimulateOrderService;
import ru.abelogur.tininvestrobot.simulator.Simulator;
import ru.abelogur.tininvestrobot.strategy.OneMinuteScalpingStrategy;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BotService {

    private final CandleService candleService;
    private final RealOrderService realOrderService;
    private final SimulateOrderService simulateOrderService;
    private final Simulator simulator;

    public void createBot(BotConfig config) {
        var groupId = CandleGroupId.of(config.getFigi(), CandleInterval.CANDLE_INTERVAL_1_MIN);
        var candles = candleService.loadHistoricCandles(groupId);
        var candleList = new ArrayList<>(candles);
        var investStrategy = new OneMinuteScalpingStrategy(candleList);
        var bot = new InvestBot(UUID.randomUUID(), config.getFigi(), candleList, investStrategy,
                realOrderService, config.getTakeProfit(), config.getStopLoss());
        candleService.addObserver(groupId, bot);

    }

    public UUID createBotSimulation(BotConfig config, Instant startSimulation) {
        var groupId = CandleGroupId.of(config.getFigi(), CandleInterval.CANDLE_INTERVAL_1_MIN);
        var candles = candleService.loadHistoricCandles(groupId, startSimulation);
        var candleList = new ArrayList<>(candles);
        var investStrategy = new OneMinuteScalpingStrategy(candleList);
        var botUuid = UUID.randomUUID();
        var bot = new InvestBot(botUuid, config.getFigi(), candleList, investStrategy,
                simulateOrderService, config.getTakeProfit(), config.getStopLoss());
        candleService.addObserver(groupId, bot);
        simulator.simulate(groupId, startSimulation);
        candleService.uncached(groupId);
        candleService.removeObserver(groupId, bot);
        return botUuid;
    }
}
