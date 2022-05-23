package ru.abelogur.tininvestrobot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.abelogur.tininvestrobot.dto.chart.Chart;
import ru.abelogur.tininvestrobot.dto.chart.ChartIndicators;
import ru.abelogur.tininvestrobot.service.IndicatorService;

import java.util.UUID;

@Tag(name = "Индикатор")
@RestController
@RequestMapping("indicator")
@RequiredArgsConstructor
public class IndicatorController {

    private final IndicatorService indicatorService;

    @Operation(summary = "Исторических данные цены инструмента и значений его индикаторов")
    @GetMapping("bot/{botUuid}")
    public ResponseEntity<Chart> getIndicators(@PathVariable UUID botUuid,
                                               @Parameter(description = "Кол-во смещений на размер периода")
                                               @RequestParam(required = false, defaultValue = "0") Integer offset,
                                               @Parameter(description = "Размер периода, за который берутся данные в днях")
                                               @RequestParam(required = false, defaultValue = "1") Integer periodDays) {
        return indicatorService.getIndicators(botUuid, offset, periodDays)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(summary = "Последние данные цены инструмента и значений его индикаторов")
    @GetMapping("bot/{botUuid}/last")
    public ChartIndicators getLastIndicators(@PathVariable UUID botUuid) {
        return indicatorService.getLastIndicators(botUuid);
    }
}
