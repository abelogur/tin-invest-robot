package ru.abelogur.tininvestrobot.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.abelogur.tininvestrobot.repository.CurrencyRepository;
import ru.abelogur.tininvestrobot.service.SdkService;
import ru.tinkoff.piapi.contract.v1.Currency;
import ru.tinkoff.piapi.contract.v1.InstrumentStatus;
import ru.tinkoff.piapi.contract.v1.LastPrice;
import ru.tinkoff.piapi.core.utils.MapperUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class CurrencyInMemoryRepository implements CurrencyRepository {

    private final String RUB_ISO = "rub";

    private final Map<String, String> isoToFigiMap;
    private final SdkService sdkService;

    public CurrencyInMemoryRepository(SdkService sdkService) {
        this.sdkService = sdkService;
        this.isoToFigiMap = sdkService.getInvestApi().getInstrumentsService()
                .getCurrenciesSync(InstrumentStatus.INSTRUMENT_STATUS_BASE).stream()
                .collect(Collectors.toMap(Currency::getIsoCurrencyName, Currency::getFigi));
    }

    @Override
    public BigDecimal getCurrentPrice(String isoCode) {
        if (isoCode.equalsIgnoreCase(RUB_ISO)) {
            return BigDecimal.ONE;
        }
        var figi = isoToFigiMap.get(isoCode);
        LastPrice lastPrice = sdkService.getInvestApi().getMarketDataService().getLastPricesSync(List.of(figi)).get(0);
        return MapperUtils.quotationToBigDecimal(lastPrice.getPrice());
    }
}
