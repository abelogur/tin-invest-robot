package ru.abelogur.tininvestrobot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.abelogur.tininvestrobot.dto.StatisticDto;
import ru.abelogur.tininvestrobot.service.StatisticService;

import java.util.UUID;

@RestController
@RequestMapping("statistic")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping("bot/{botUuid}")
    public StatisticDto getStatistic(@PathVariable UUID botUuid) {
        return statisticService.getStatistic(botUuid);
    }

    @GetMapping("general")
    public StatisticDto getGeneralStatistic() {
        return statisticService.getGeneralStatistic();
    }
}
