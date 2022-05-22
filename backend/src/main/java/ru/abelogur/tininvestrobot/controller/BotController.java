package ru.abelogur.tininvestrobot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.abelogur.tininvestrobot.dto.BotConfig;
import ru.abelogur.tininvestrobot.dto.BotPreview;
import ru.abelogur.tininvestrobot.service.BotService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Tag(name = "Инвест Бот")
@RestController
@RequestMapping("bot")
@RequiredArgsConstructor
public class BotController {

    private final BotService botService;

    @Operation(summary = "Получение всех ботов")
    @GetMapping
    public List<BotPreview> getAll() {
        return botService.getBotsPreview();
    }

    @Operation(summary = "Создание бота на \"боевом\" окружении")
    @PostMapping
    public BotPreview createBot(@RequestBody BotConfig config) {
        return botService.createRealBot(config);
    }

    @Operation(summary = "Создание бота в песочнице")
    @PostMapping("sandbox")
    public BotPreview createSandbox(@RequestBody BotConfig config) {
        return botService.createSandboxBot(config);
    }

    @Operation(summary = "Создание бота на исторических данных (симуляция)")
    @PostMapping("simulation")
    public BotPreview createSimulationBot(@RequestBody BotConfig config, @RequestParam Instant start) {
        return botService.createBotSimulation(config, start);
    }

    @Operation(summary = "Удаление бота")
    @DeleteMapping("{botUuid}")
    public void removeBot(@PathVariable UUID botUuid) {
        botService.removeBot(botUuid);
    }
}
