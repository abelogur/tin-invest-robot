package ru.abelogur.tininvestrobot.repository;

import ru.abelogur.tininvestrobot.domain.CachedInstrument;

import java.util.List;

public interface InstrumentRepository {
    CachedInstrument get(String figi);

    List<CachedInstrument> getAllShare();

    void add(String figi);
}
