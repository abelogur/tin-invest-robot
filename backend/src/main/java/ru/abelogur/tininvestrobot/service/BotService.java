package ru.abelogur.tininvestrobot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.controller.exception.RestRuntimeException;
import ru.abelogur.tininvestrobot.domain.*;
import ru.abelogur.tininvestrobot.dto.BotConfig;
import ru.abelogur.tininvestrobot.dto.BotEnv;
import ru.abelogur.tininvestrobot.dto.BotPreview;
import ru.abelogur.tininvestrobot.dto.StatisticDto;
import ru.abelogur.tininvestrobot.repository.InstrumentRepository;
import ru.abelogur.tininvestrobot.repository.InvestBotRepository;
import ru.abelogur.tininvestrobot.service.candle.CandleService;
import ru.abelogur.tininvestrobot.service.candle.CandleSteamsHolder;
import ru.abelogur.tininvestrobot.service.notification.TelegramNotification;
import ru.abelogur.tininvestrobot.service.order.*;
import ru.abelogur.tininvestrobot.simulator.Simulator;
import ru.tinkoff.piapi.contract.v1.Account;
import ru.tinkoff.piapi.core.exception.ApiRuntimeException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static ru.tinkoff.piapi.contract.v1.AccessLevel.ACCOUNT_ACCESS_LEVEL_FULL_ACCESS;
import static ru.tinkoff.piapi.contract.v1.AccountStatus.ACCOUNT_STATUS_OPEN;

@Service
@RequiredArgsConstructor
public class BotService {

    private final SdkService sdkService;
    private final InvestBotRepository investBotRepository;
    private final InstrumentRepository instrumentRepository;
    private final CandleService candleService;
    private final StatisticService statisticService;
    private final OrderObserversHolder orderObserversHolder;
    private final CandleSteamsHolder candleSteamsHolder;
    private final Simulator simulator;

    private final RealOrderService realOrderService;
    private final SandboxOrderService sandboxOrderService;
    private final SimulateOrderService simulateOrderService;

    private Optional<TelegramNotification> telegramNotification = Optional.empty();

    public List<BotPreview> getBotsPreview() {
        return investBotRepository.getAll().stream()
                .map(this::getBotPreview)
                .collect(Collectors.toList());
    }

    public BotPreview createRealBot(BotConfig config) {
        var groupId = CandleGroupId.of(config.getFigi(), config.getStrategyCode().getInterval());
        var candles = candleService.loadHistoricCandles(groupId);
        var accountId = getRealAccountId(config);
        var state = getBotState(config, accountId, BotEnv.REAL, groupId, isMarginAvailable(accountId));
        var bot = createBot(candles, state, config, realOrderService);
        candleSteamsHolder.addNewSubscription(groupId);
        return getBotPreview(bot);
    }

    public BotPreview createSandboxBot(BotConfig config) {
        var groupId = CandleGroupId.of(config.getFigi(), config.getStrategyCode().getInterval());
        var candles = candleService.loadHistoricCandles(groupId);
        var accountId = getSandboxAccountId(config);
        var state = getBotState(config, accountId, BotEnv.SANDBOX, groupId, true);
        var bot = createBot(candles, state, config, sandboxOrderService);
        candleSteamsHolder.addNewSubscription(groupId);
        return getBotPreview(bot);
    }

    public BotPreview createBotSimulation(BotConfig config, Instant startSimulation) {
        var groupId = CandleGroupId.of(config.getFigi(), config.getStrategyCode().getInterval());
        var candles = candleService.loadHistoricCandles(groupId, startSimulation);
        var state = getBotState(config, "simulation", BotEnv.SIMULATION, groupId, true);
        var bot = createBot(candles, state, config, simulateOrderService);
        simulator.simulate(groupId, startSimulation);
        candleService.uncached(groupId);
        candleService.removeObserver(groupId, bot);
        return getBotPreview(bot);
    }

    public void removeBot(UUID botUuid) {
        investBotRepository.get(botUuid)
                .ifPresent(bot -> {
                    var groupId = bot.getState().getGroupId();
                    candleService.uncached(groupId);
                    candleService.removeObserver(groupId, bot);
                    investBotRepository.remove(botUuid);
                    candleSteamsHolder.removeSubscription(groupId);
                    orderObserversHolder.removeSpecifiedObserver(botUuid);
                    telegramNotification.ifPresent(it -> it.removeObserver(botUuid));
                });
    }

    private InvestBot createBot(SortedSet<CachedCandle> candles, BotState state, BotConfig config, OrderService orderService) {
        var candleList = new ArrayList<>(candles);
        var investStrategy = config.getStrategyCode().create(candleList, config.getStrategiesConfig());
        var bot = new InvestBot(state, candleList, investStrategy, orderService);
        candleService.addObserver(state.getGroupId(), bot);
        orderObserversHolder.addSpecifiedObserver(state.getUuid(), bot);
        if (config.getTelegramBotChatId() != null) {
            telegramNotification
                    .ifPresent(it -> it.addObserver(bot.getState().getUuid(), config.getTelegramBotChatId()));
        }
        investBotRepository.save(bot);
        return bot;
    }

    private String getRealAccountId(BotConfig config) {
        return Optional.ofNullable(config.getAccountId())
                .or(() -> sdkService.getInvestApi().getUserService().getAccountsSync().stream()
                        .filter(account -> account.getStatus().equals(ACCOUNT_STATUS_OPEN)
                                && account.getAccessLevel().equals(ACCOUNT_ACCESS_LEVEL_FULL_ACCESS))
                        .findFirst().map(Account::getId))
                .orElseThrow(() -> new RestRuntimeException("Нет подходящего аккаунта", HttpStatus.NOT_FOUND));
    }

    private String getSandboxAccountId(BotConfig config) {
        return Optional.ofNullable(config.getAccountId())
                .or(() -> sdkService.getSandboxInvestApi().getSandboxService().getAccountsSync().stream()
                        .filter(account -> account.getStatus().equals(ACCOUNT_STATUS_OPEN)
                                && account.getAccessLevel().equals(ACCOUNT_ACCESS_LEVEL_FULL_ACCESS))
                        .findFirst().map(Account::getId))
                .or(() -> Optional.of(sdkService.getSandboxInvestApi().getSandboxService().openAccountSync()))
                .orElseThrow(() -> new RestRuntimeException("Нет подходящего аккаунта", HttpStatus.NOT_FOUND));
    }

    private BotState getBotState(BotConfig config, String accountId, BotEnv env,
                                 CandleGroupId groupId, boolean isMarginAvailable) {
        return new BotState()
                .setUuid(UUID.randomUUID())
                .setGroupId(groupId)
                .setInitTime(Instant.now())
                .setAccountId(accountId)
                .setBotEnv(env)
                .setTakeProfit(config.getTakeProfit())
                .setStopLoss(config.getStopLoss())
                .setNumberOfLots(config.getNumberOfLots())
                .setMarginAvailable(isMarginAvailable)
                .setTelegramBotChatId(config.getTelegramBotChatId());
    }

    private boolean isMarginAvailable(String accountId) {
        try {
            sdkService.getInvestApi().getUserService().getMarginAttributesSync(accountId);
            return true;
        } catch (ApiRuntimeException e) {
            return false;
        }
    }

    private BotPreview getBotPreview(InvestBot bot) {
        BotState state = bot.getState();
        CachedInstrument instrument = instrumentRepository.get(state.getFigi());
        StatisticDto statistic = statisticService.getStatistic(bot.getState().getUuid());
        return new BotPreview()
                .setUuid(state.getUuid())
                .setStart(bot.getState().getInitTime())
                .setStrategyCode(bot.getInvestStrategy().getCode())
                .setBotEnv(state.getBotEnv())
                .setInstrument(instrument.getName())
                .setInstrumentTicket(instrument.getTicker())
                .setNumberOfOrders(statistic.getOrders().size())
                .setProfit(statistic.getProfit())
                .setProfitPercentage(statistic.getProfitPercentage())
                .setCurrency(instrument.getCurrency())
                .setTelegramBotChatId(state.getTelegramBotChatId())
                .setIconUrl(instrument.getImage())
                .setErrors(bot.getState().getErrors());
    }

    @Autowired(required = false)
    public void setTelegramNotification(TelegramNotification telegramNotification) {
        this.telegramNotification = Optional.of(telegramNotification);
    }
}
