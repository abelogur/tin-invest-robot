package ru.abelogur.tininvestrobot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.domain.CachedCandle;
import ru.abelogur.tininvestrobot.domain.CandleGroupId;
import ru.abelogur.tininvestrobot.domain.InvestBot;
import ru.abelogur.tininvestrobot.dto.BotConfig;
import ru.abelogur.tininvestrobot.dto.BotSettings;
import ru.abelogur.tininvestrobot.helper.CandleSteamsHolder;
import ru.abelogur.tininvestrobot.helper.OrderObserversHolder;
import ru.abelogur.tininvestrobot.repository.InvestBotRepository;
import ru.abelogur.tininvestrobot.service.order.OrderService;
import ru.abelogur.tininvestrobot.service.order.RealOrderService;
import ru.abelogur.tininvestrobot.service.order.SandboxOrderService;
import ru.abelogur.tininvestrobot.service.order.SimulateOrderService;
import ru.abelogur.tininvestrobot.simulator.Simulator;
import ru.abelogur.tininvestrobot.strategy.OneMinuteScalpingStrategy;
import ru.tinkoff.piapi.contract.v1.Account;
import ru.tinkoff.piapi.contract.v1.CandleInterval;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.SortedSet;
import java.util.UUID;

import static ru.tinkoff.piapi.contract.v1.AccessLevel.ACCOUNT_ACCESS_LEVEL_FULL_ACCESS;
import static ru.tinkoff.piapi.contract.v1.AccountStatus.ACCOUNT_STATUS_OPEN;

@Service
@RequiredArgsConstructor
public class BotService {

    private final SdkService sdkService;
    private final CandleService candleService;
    private final RealOrderService realOrderService;
    private final SandboxOrderService sandboxOrderService;
    private final SimulateOrderService simulateOrderService;
    private final Simulator simulator;
    private final OrderObserversHolder orderObserversHolder;
    private final InvestBotRepository investBotRepository;
    private final CandleSteamsHolder candleSteamsHolder;

    public UUID createRealBot(BotConfig config) {
        var groupId = CandleGroupId.of(config.getFigi(), CandleInterval.CANDLE_INTERVAL_1_MIN);
        var candles = candleService.loadHistoricCandles(groupId);
        var accountId = getRealAccountId(config);
        var settings = getBotSettings(config, accountId);
        return createBot(groupId, candles, settings, realOrderService).getSettings().getUuid();
    }

    public UUID createSandboxBot(BotConfig config) {
        var groupId = CandleGroupId.of(config.getFigi(), CandleInterval.CANDLE_INTERVAL_1_MIN);
        var candles = candleService.loadHistoricCandles(groupId);
        var accountId = getSandboxAccountId(config);
        var settings = getBotSettings(config, accountId);
        candleSteamsHolder.addNewSubscription(groupId);
        return createBot(groupId, candles, settings, sandboxOrderService).getSettings().getUuid();
    }

    public UUID createBotSimulation(BotConfig config, Instant startSimulation) {
        var groupId = CandleGroupId.of(config.getFigi(), CandleInterval.CANDLE_INTERVAL_1_MIN);
        var candles = candleService.loadHistoricCandles(groupId, startSimulation);

        var settings = getBotSettings(config, "simulation");
        var bot = createBot(groupId, candles, settings, simulateOrderService);
        simulator.simulate(groupId, startSimulation);
        candleService.uncached(groupId);
        candleService.removeObserver(groupId, bot);
        return bot.getSettings().getUuid();
    }

    private InvestBot createBot(CandleGroupId groupId, SortedSet<CachedCandle> candles,
                           BotSettings settings, OrderService orderService) {
        var candleList = new ArrayList<>(candles);
        var investStrategy = new OneMinuteScalpingStrategy(candleList);
        var bot = new InvestBot(settings, candleList, investStrategy, orderService);
        candleService.addObserver(groupId, bot);
        orderObserversHolder.addSpecifiedObserver(settings.getUuid(), bot);
        investBotRepository.saveBotSettings(settings);
        return bot;
    }

    private String getRealAccountId(BotConfig config) {
        return Optional.ofNullable(config.getAccountId())
                .or(() -> sdkService.getInvestApi().getUserService().getAccountsSync().stream()
                        .filter(account -> account.getStatus().equals(ACCOUNT_STATUS_OPEN)
                                && account.getAccessLevel().equals(ACCOUNT_ACCESS_LEVEL_FULL_ACCESS))
                        .findFirst().map(Account::getId))
                .orElseThrow(() -> new IllegalArgumentException("There aren't accounts"));
    }

    private String getSandboxAccountId(BotConfig config) {
        return Optional.ofNullable(config.getAccountId())
                .or(() -> sdkService.getSandboxInvestApi().getSandboxService().getAccountsSync().stream()
                        .filter(account -> account.getStatus().equals(ACCOUNT_STATUS_OPEN)
                                && account.getAccessLevel().equals(ACCOUNT_ACCESS_LEVEL_FULL_ACCESS))
                        .findFirst().map(Account::getId))
                .orElseThrow(() -> new IllegalArgumentException("There aren't accounts"));
    }

    private BotSettings getBotSettings(BotConfig config, String accountId) {
        return new BotSettings()
                .setUuid(UUID.randomUUID())
                .setAccountId(accountId)
                .setFigi(config.getFigi())
                .setTakeProfit(config.getTakeProfit())
                .setStopLoss(config.getStopLoss())
                .setNumberOfLots(config.getNumberOfLots());
    }
}
