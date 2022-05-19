package ru.abelogur.tininvestrobot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.abelogur.tininvestrobot.dto.StrategyInfo;
import ru.abelogur.tininvestrobot.strategy.StrategyCode;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("strategy")
public class StrategyController {

    @GetMapping
    public List<StrategyInfo> getAll() {
        return Arrays.stream(StrategyCode.values())
                .map(StrategyCode::toDto)
                .collect(Collectors.toList());
    }
}
