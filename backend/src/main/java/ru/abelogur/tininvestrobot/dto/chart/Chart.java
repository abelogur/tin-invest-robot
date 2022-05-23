package ru.abelogur.tininvestrobot.dto.chart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class Chart {
    @Schema(description = "Цена инструмента")
    private List<ChartPoint> points;

    @Schema(description = "Значения индикаторов")
    private Map<String, List<ChartPoint>> indicators;
}
