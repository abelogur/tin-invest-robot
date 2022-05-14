package ru.abelogur.tininvestrobot.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.abelogur.tininvestrobot.controller.dto.BotConfig;

@RestController
@RequestMapping("bot")
public class BotController {

    @PostMapping
    public void createBot(@RequestBody BotConfig config) {

    }
}
