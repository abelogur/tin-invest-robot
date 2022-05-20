package ru.abelogur.tininvestrobot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.abelogur.tininvestrobot.domain.Order;
import ru.abelogur.tininvestrobot.dto.CreateOrderInfo;
import ru.tinkoff.piapi.core.exception.ApiRuntimeException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
public class TelegramNotification implements OrderObserver {

    private final String START_COMMAND = "/start";

    private final Map<UUID, String> investBotToChatId = new HashMap<>();

    private final TelegramBot telegramBot;

    public TelegramNotification(@Value("${app.telegram.token}") String telegramBotToken) {
        this.telegramBot = new TelegramBot(telegramBotToken);
        updatesListener();
    }

    public void addObserver(UUID botUuid, String chatId) {
        investBotToChatId.put(botUuid, chatId);
    }

    public void removeObserver(UUID botUuid) {
        investBotToChatId.remove(botUuid);
    }

    public void notifyNewOrder(Order order) {
        if (investBotToChatId.containsKey(order.getBotUuid())) {
            var message = String.format(
                    "Новая заявка %s! %s %s. %s %s шт. по цене %s. Причина %s",
                    order.getId(),
                    order.getAction(),
                    order.getType(),
                    order.getInstrumentName(),
                    order.getNumberOfLots(),
                    order.getPrice(),
                    order.getReason()
            );
            CompletableFuture.runAsync(() ->
                    telegramBot.execute(new SendMessage(investBotToChatId.get(order.getBotUuid()), message)));
        }
    }

    public void notifySuccessfulOrder(Order order) {
        if (investBotToChatId.containsKey(order.getBotUuid())) {
            var message = String.format(
                    "Заявка выполнена %s! Итоговая цена %s",
                    order.getId(),
                    order.getPrice()
            );
            CompletableFuture.runAsync(() ->
                    telegramBot.execute(new SendMessage(investBotToChatId.get(order.getBotUuid()), message)));
        }
    }

    public void notifyFailedOrder(Order order) {
        if (investBotToChatId.containsKey(order.getBotUuid())) {
            var message = String.format(
                    "Заявка отклонена %s!",
                    order.getId()
            );
            CompletableFuture.runAsync(() ->
                    telegramBot.execute(new SendMessage(investBotToChatId.get(order.getBotUuid()), message)));
        }
    }

    public void notifyError(CreateOrderInfo info, ApiRuntimeException e) {
        if (investBotToChatId.containsKey(info.getBotUuid())) {
            var message = String.format(
                    "Ошибка выставления заявки! %s",
                    e.getMessage()
            );
            CompletableFuture.runAsync(() ->
                    telegramBot.execute(new SendMessage(investBotToChatId.get(info.getBotUuid()), message)));
        }
    }

    private void updatesListener() {
        telegramBot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                if (update.message().text().equals(START_COMMAND)) {
                    long chatId = update.message().chat().id();
                    telegramBot.execute(new SendMessage(chatId, "Привет! Твой chatId " + chatId));
                }
            });

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
