package ru.abelogur.tininvestrobot.service.notification.socket;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.abelogur.tininvestrobot.domain.*;
import ru.abelogur.tininvestrobot.dto.CreateOrderInfo;
import ru.abelogur.tininvestrobot.repository.InvestBotRepository;
import ru.abelogur.tininvestrobot.service.IndicatorService;
import ru.abelogur.tininvestrobot.service.candle.CandleService;
import ru.abelogur.tininvestrobot.service.notification.socket.dto.OrderErrorEvent;
import ru.abelogur.tininvestrobot.service.notification.socket.dto.OrderEvent;
import ru.abelogur.tininvestrobot.service.order.OrderObserver;
import ru.tinkoff.piapi.core.exception.ApiRuntimeException;

import java.util.*;

import static ru.abelogur.tininvestrobot.service.notification.socket.SocketOrderEvents.*;

@Slf4j
@Service
public class SocketService implements OrderObserver, CandleObserver {

    private final HashMap<CandleGroupId, List<InvestBot>> investBots = new HashMap<>();

    private final SocketIOServer socketIOServer;
    private final InvestBotRepository investBotRepository;
    private final IndicatorService indicatorService;

    public SocketService(CandleService candleService, InvestBotRepository investBotRepository,
                         IndicatorService indicatorService) {
        this.investBotRepository = investBotRepository;
        this.indicatorService = indicatorService;

        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        this.socketIOServer = new SocketIOServer(config);
        this.socketIOServer.start();

        addIndicatorsSubscribeListener(candleService);
    }

    @Override
    public void notifyNewOrder(Order order) {
        socketIOServer.getBroadcastOperations()
                .sendEvent(NEW.getTopic(), OrderEvent.of(order));
    }

    @Override
    public void notifySuccessfulOrder(Order order) {
        socketIOServer.getBroadcastOperations()
                .sendEvent(SUCCESSFUL.getTopic(), OrderEvent.of(order));
    }

    @Override
    public void notifyFailedOrder(Order order) {
        socketIOServer.getBroadcastOperations()
                .sendEvent(FAILED.getTopic(), OrderEvent.of(order));
    }

    @Override
    public void notifyError(CreateOrderInfo info, ApiRuntimeException e) {
        socketIOServer.getBroadcastOperations()
                .sendEvent(ERROR.getTopic(), new OrderErrorEvent(e.getMessage(), info.getReason()));
    }

    @Override
    public void notifyCandle(CandleGroupId groupId, CachedCandle candle) {
        investBots.getOrDefault(groupId, Collections.emptyList())
                .forEach(bot -> socketIOServer.getRoomOperations(bot.getState().getUuid().toString())
                        .sendEvent(IndicatorEvents.INDICATORS.getTopic(), indicatorService.getLastIndicators(bot.getState().getUuid())));
    }

    private void addIndicatorsSubscribeListener(CandleService candleService) {
        this.socketIOServer.addEventListener(IndicatorEvents.INDICATORS_SUBSCRIBE.getTopic(), UUID.class,
                (client, botUuid, ackSender) -> investBotRepository.get(botUuid)
                        .ifPresent(bot -> {
                            var groupId = bot.getState().getGroupId();
                            candleService.addObserver(groupId, this);

                            investBots.putIfAbsent(groupId, new ArrayList<>());
                            investBots.get(groupId).add(bot);

                            log.info("Bot {} was subscribed", bot.getState().getUuid());
                        }));
    }
}
