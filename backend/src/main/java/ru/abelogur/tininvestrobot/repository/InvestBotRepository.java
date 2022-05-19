package ru.abelogur.tininvestrobot.repository;

import ru.abelogur.tininvestrobot.domain.CandleGroupId;
import ru.abelogur.tininvestrobot.domain.InvestBot;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvestBotRepository {
    Optional<InvestBot> get(UUID botUuid);

    List<InvestBot> getAll();

    void save(InvestBot bot);

    void remove(UUID botUuid);

    List<InvestBot> getByGroupId(CandleGroupId groupId);
}
