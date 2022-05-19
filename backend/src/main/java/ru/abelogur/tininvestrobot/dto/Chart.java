package ru.abelogur.tininvestrobot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class Chart {
    private List<ChartPoint> candles;
    private Map<String, List<ChartPoint>> indicators;
}
