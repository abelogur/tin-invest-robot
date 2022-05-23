package ru.abelogur.tininvestrobot.dto.chart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ChartIndicators {
    @Schema(description = "Цена инструмента")
    private ChartPoint point;

    @Schema(description = "Значения индикаторов")
    private Map<String, ChartPoint> indicators;
}
