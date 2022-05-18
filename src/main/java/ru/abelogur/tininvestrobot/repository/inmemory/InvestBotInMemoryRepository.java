package ru.abelogur.tininvestrobot.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.abelogur.tininvestrobot.dto.BotSettings;
import ru.abelogur.tininvestrobot.repository.InvestBotRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InvestBotInMemoryRepository implements InvestBotRepository {

    private final Map<UUID, BotSettings> settingsMap = new HashMap<>();

    @Override
    public Optional<BotSettings> getBotSettings(UUID botUuid) {
        return Optional.ofNullable(settingsMap.get(botUuid));
    }

    @Override
    public void saveBotSettings(BotSettings settings) {
        settingsMap.put(settings.getUuid(), settings);
    }
}
