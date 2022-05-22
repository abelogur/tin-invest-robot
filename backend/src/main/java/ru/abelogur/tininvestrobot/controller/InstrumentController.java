package ru.abelogur.tininvestrobot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.abelogur.tininvestrobot.domain.CachedInstrument;
import ru.abelogur.tininvestrobot.repository.InstrumentRepository;

import java.util.List;

@Tag(name = "Инструмент")
@RestController
@RequestMapping("instrument")
@RequiredArgsConstructor
public class InstrumentController {

    private final InstrumentRepository instrumentRepository;

    @Operation(summary = "Получение инструмента по figi")
    @GetMapping("{figi}")
    public CachedInstrument get(@PathVariable String figi) {
        return instrumentRepository.get(figi);
    }

    @Operation(summary = "Все акций")
    @GetMapping
    public List<CachedInstrument> getAllShares() {
        return instrumentRepository.getAllShare();
    }
}
