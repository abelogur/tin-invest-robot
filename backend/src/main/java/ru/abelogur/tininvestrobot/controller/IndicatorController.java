package ru.abelogur.tininvestrobot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.abelogur.tininvestrobot.dto.ChartPoint;
import ru.abelogur.tininvestrobot.service.IndicatorService;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("indicator")
@RequiredArgsConstructor
public class IndicatorController {

    private final IndicatorService indicatorService;

    @GetMapping("/ema")
    public List<ChartPoint> getEmaIndicator(@RequestParam String figi,
                                            @RequestParam Integer counter,
                                            @RequestParam Duration interval) {
        return indicatorService.getEmaIndicator(figi, counter, interval);
    }

    @GetMapping("/stochasticOscillator")
    public List<ChartPoint> geStochasticOscillatorIndicator(@RequestParam String figi,
                                                            @RequestParam Integer counter,
                                                            @RequestParam Duration interval,
                                                            @RequestParam Integer smoothing) {
        return indicatorService.getStochasticOscillatorIndicator(figi, counter, interval, smoothing);
    }
}
