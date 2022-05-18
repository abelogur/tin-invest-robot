package ru.abelogur.tininvestrobot.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.abelogur.tininvestrobot.dto.BotSettings;
import ru.abelogur.tininvestrobot.repository.InvestBotRepository;

import java.util.*;

@Repository
public class InvestBotInMemoryRepository implements InvestBotRepository {

    private final Map<UUID, BotSettings> settingsMap = new HashMap<>();

    @Override
    public Optional<BotSettings> getBotSettings(UUID botUuid) {
        return Optional.ofNullable(settingsMap.get(botUuid));
    }

    @Override
    public List<BotSettings> getAllBotSettings() {
        return new ArrayList<>(settingsMap.values());
    }

    @Override
    public void saveBotSettings(BotSettings settings) {
        settingsMap.put(settings.getUuid(), settings);
    }
}
