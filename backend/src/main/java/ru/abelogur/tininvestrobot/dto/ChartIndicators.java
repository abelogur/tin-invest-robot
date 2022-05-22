package ru.abelogur.tininvestrobot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ChartIndicators {
    private ChartPoint point;
    private Map<String, ChartPoint> indicators;
}
