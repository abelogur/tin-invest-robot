package ru.abelogur.tininvestrobot.domain;

import com.google.protobuf.Timestamp;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.tinkoff.piapi.contract.v1.Candle;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.core.utils.MapperUtils;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Accessors(chain = true)
public class CachedCandle {
    private BigDecimal closePrice;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private Instant time;

    private CachedCandle(Quotation closePrice, Quotation openPrice, Quotation highPrice, Quotation lowPrice,
                         Timestamp timestamp, BigDecimal lot) {
        this.closePrice = MapperUtils.quotationToBigDecimal(closePrice).multiply(lot);
        this.openPrice = MapperUtils.quotationToBigDecimal(openPrice).multiply(lot);
        this.highPrice = MapperUtils.quotationToBigDecimal(highPrice).multiply(lot);
        this.lowPrice = MapperUtils.quotationToBigDecimal(lowPrice).multiply(lot);
        this.time = Instant.ofEpochSecond(timestamp.getSeconds());
    }

    public static CachedCandle ofHistoricCandle(HistoricCandle candle, BigDecimal lot) {
        return new CachedCandle(candle.getClose(), candle.getOpen(), candle.getHigh(),
                candle.getLow(), candle.getTime(), lot);
    }

    public static CachedCandle ofStreamCandle(Candle candle, BigDecimal lot) {
        return new CachedCandle(candle.getClose(), candle.getOpen(), candle.getHigh(),
                candle.getLow(), candle.getTime(), lot);
    }
}
