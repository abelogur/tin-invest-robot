package ru.abelogur.tininvestrobot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.abelogur.tininvestrobot.dto.BotConfig;
import ru.abelogur.tininvestrobot.dto.BotPreview;
import ru.abelogur.tininvestrobot.service.BotService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("bot")
@RequiredArgsConstructor
public class BotController {

    private final BotService botService;

    @GetMapping
    public List<BotPreview> getAll() {
        return botService.getBotsPreview();
    }

    @PostMapping
    public UUID createBot(@RequestBody BotConfig config) {
        return botService.createRealBot(config);
    }

    @PostMapping("sandbox")
    public UUID createSandbox(@RequestBody BotConfig config) {
        return botService.createSandboxBot(config);
    }

    @PostMapping("simulation")
    public UUID createSimulationBot(@RequestBody BotConfig config, @RequestParam Instant start) {
        return botService.createBotSimulation(config, start);
    }
}
