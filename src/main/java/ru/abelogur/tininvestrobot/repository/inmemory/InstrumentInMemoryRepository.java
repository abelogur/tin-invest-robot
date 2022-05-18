package ru.abelogur.tininvestrobot.repository.inmemory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.abelogur.tininvestrobot.domain.CachedInstrument;
import ru.abelogur.tininvestrobot.repository.InstrumentRepository;
import ru.abelogur.tininvestrobot.service.SdkService;

import java.util.HashMap;

@Repository
@RequiredArgsConstructor
public class InstrumentInMemoryRepository implements InstrumentRepository {

    private final HashMap<String, CachedInstrument> instruments = new HashMap<>();

    private final SdkService sdkService;

    @Override
    public CachedInstrument get(String figi) {
        if (!instruments.containsKey(figi)) {
            add(figi);
        }
        return instruments.get(figi);
    }

    @Override
    public void add(String figi) {
        var instrument = sdkService.getInvestApi().getInstrumentsService().getInstrumentByFigiSync(figi);
        instruments.put(figi, CachedInstrument.of(instrument));
    }
}
