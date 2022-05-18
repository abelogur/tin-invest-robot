package ru.abelogur.tininvestrobot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.abelogur.tininvestrobot.dto.BotConfig;
import ru.abelogur.tininvestrobot.dto.BotSettings;
import ru.abelogur.tininvestrobot.repository.InvestBotRepository;
import ru.abelogur.tininvestrobot.service.BotService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("bot")
@RequiredArgsConstructor
public class BotController {

    private final BotService botService;
    private final InvestBotRepository investBotRepository;

    @GetMapping
    public List<BotSettings> getSettings() {
        return investBotRepository.getAllBotSettings();
    }

    @PostMapping()
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
