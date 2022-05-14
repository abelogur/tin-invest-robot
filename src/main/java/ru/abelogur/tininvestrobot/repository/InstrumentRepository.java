package ru.abelogur.tininvestrobot.repository;

import ru.abelogur.tininvestrobot.domain.CachedInstrument;

public interface InstrumentRepository {
    CachedInstrument get(String figi);

    void add(String figi);
}
