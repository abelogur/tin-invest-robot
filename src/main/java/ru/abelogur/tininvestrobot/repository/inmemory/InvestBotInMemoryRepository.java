package ru.abelogur.tininvestrobot.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.abelogur.tininvestrobot.domain.InvestBot;
import ru.abelogur.tininvestrobot.repository.InvestBotRepository;

import java.util.*;

@Repository
public class InvestBotInMemoryRepository implements InvestBotRepository {

    private final Map<UUID, InvestBot> settingsMap = new HashMap<>();

    @Override
    public Optional<InvestBot> get(UUID botUuid) {
        return Optional.ofNullable(settingsMap.get(botUuid));
    }

    @Override
    public List<InvestBot> getAll() {
        return new ArrayList<>(settingsMap.values());
    }

    @Override
    public void save(InvestBot bot) {
        settingsMap.put(bot.getSettings().getUuid(), bot);
    }
}
