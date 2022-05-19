package ru.abelogur.tininvestrobot.repository.inmemory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.abelogur.tininvestrobot.domain.CachedInstrument;
import ru.abelogur.tininvestrobot.repository.InstrumentRepository;
import ru.abelogur.tininvestrobot.service.SdkService;
import ru.tinkoff.piapi.contract.v1.InstrumentStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InstrumentInMemoryRepository implements InstrumentRepository {

    private final List<CachedInstrument> shares = new ArrayList<>();
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
    public List<CachedInstrument> getAllShare() {
        if (!shares.isEmpty()) {
            return shares;
        }
        return sdkService.getInvestApi().getInstrumentsService().getSharesSync(InstrumentStatus.INSTRUMENT_STATUS_BASE)
                .stream()
                .map(share -> {
                    var instrument = CachedInstrument.of(share);
                    instruments.putIfAbsent(share.getFigi(), instrument);
                    return instrument;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void add(String figi) {
        var instrument = sdkService.getInvestApi().getInstrumentsService().getInstrumentByFigiSync(figi);
        instruments.put(figi, CachedInstrument.of(instrument));
    }
}
