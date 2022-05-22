package ru.abelogur.tininvestrobot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.abelogur.tininvestrobot.dto.StatisticDto;
import ru.abelogur.tininvestrobot.service.StatisticService;

import java.util.UUID;

@Tag(name = "Статистика")
@RestController
@RequestMapping("statistic")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @Operation(summary = "Статистики для бота")
    @GetMapping("bot/{botUuid}")
    public StatisticDto getStatistic(@PathVariable UUID botUuid) {
        return statisticService.getStatistic(botUuid);
    }

    @Operation(summary = "Суммарная статистика всех ботов")
    @GetMapping("general")
    public StatisticDto getGeneralStatistic() {
        return statisticService.getGeneralStatistic();
    }
}
