package ru.abelogur.tininvestrobot.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.abelogur.tininvestrobot.domain.CandleGroupId;
import ru.abelogur.tininvestrobot.domain.InvestBot;
import ru.abelogur.tininvestrobot.repository.InvestBotRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InvestBotInMemoryRepository implements InvestBotRepository {

    private final Map<UUID, InvestBot> bots = new HashMap<>();

    @Override
    public Optional<InvestBot> get(UUID botUuid) {
        return Optional.ofNullable(bots.get(botUuid));
    }

    @Override
    public List<InvestBot> getAll() {
        return new ArrayList<>(bots.values());
    }

    @Override
    public void save(InvestBot bot) {
        bots.put(bot.getSettings().getUuid(), bot);
    }

    @Override
    public void remove(UUID botUuid) {
        bots.remove(botUuid);
    }

    @Override
    public List<InvestBot> getByGroupId(CandleGroupId groupId) {
        return bots.values().stream()
                .filter(bot -> bot.getGroupId().equals(groupId))
                .collect(Collectors.toList());
    }
}
