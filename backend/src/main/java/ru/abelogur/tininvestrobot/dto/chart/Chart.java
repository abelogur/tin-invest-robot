package ru.abelogur.tininvestrobot.dto.chart;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class Chart {
    private List<ChartPoint> points;
    private Map<String, List<ChartPoint>> indicators;
}
