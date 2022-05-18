package ru.abelogur.tininvestrobot.repository;

import ru.abelogur.tininvestrobot.dto.BotSettings;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvestBotRepository {
    Optional<BotSettings> getBotSettings(UUID botUuid);

    List<BotSettings> getAllBotSettings();

    void saveBotSettings(BotSettings settings);
}
