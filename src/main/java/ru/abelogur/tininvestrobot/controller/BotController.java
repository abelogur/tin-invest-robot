package ru.abelogur.tininvestrobot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.abelogur.tininvestrobot.controller.dto.BotConfig;
import ru.abelogur.tininvestrobot.service.BotService;

import java.time.Instant;

@RestController
@RequestMapping("bot")
@RequiredArgsConstructor
public class BotController {

    private final BotService botService;

    @PostMapping()
    public void createBot(@RequestBody BotConfig config) {
        botService.createBot(config);
    }

    @PostMapping("simulation")
    public void createBot(@RequestBody BotConfig config, @RequestParam Instant start) {
        botService.createBotSimulation(config, start);
    }
}
