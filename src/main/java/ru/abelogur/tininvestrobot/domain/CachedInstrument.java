package ru.abelogur.tininvestrobot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.tinkoff.piapi.contract.v1.Instrument;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.utils.MapperUtils;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CachedInstrument {
    private static String IMAGE_URL_MASK = "https://invest-brands.cdn-tinkoff.ru/%sx640.png";

    private String figi;
    private String ticker;
    private String classCode;
    private BigDecimal lot;
    private String currency;
    private String name;
    private String instrumentType;
    private int tradingStatus;
    private boolean apiTradeAvailableFlag;
    private String image;
    private BigDecimal minPriceIncrement;

    public boolean isNormalTrading() {
        return tradingStatus == 5;
    }

    public static CachedInstrument of(Instrument instrument) {
        return new CachedInstrument(
                instrument.getFigi(),
                instrument.getTicker(),
                instrument.getClassCode(),
                BigDecimal.valueOf(instrument.getLot()),
                instrument.getCurrency(),
                instrument.getName(),
                instrument.getInstrumentType(),
                instrument.getTradingStatusValue(),
                instrument.getApiTradeAvailableFlag(),
                String.format(IMAGE_URL_MASK, instrument.getIsin()),
                MapperUtils.quotationToBigDecimal(instrument.getMinPriceIncrement())
        );
    }

    public static CachedInstrument of(Share instrument) {
        return new CachedInstrument(
                instrument.getFigi(),
                instrument.getTicker(),
                instrument.getClassCode(),
                BigDecimal.valueOf(instrument.getLot()),
                instrument.getCurrency(),
                instrument.getName(),
                "share",
                instrument.getTradingStatusValue(),
                instrument.getApiTradeAvailableFlag(),
                String.format(IMAGE_URL_MASK, instrument.getIsin()),
                MapperUtils.quotationToBigDecimal(instrument.getMinPriceIncrement())
        );
    }
}
