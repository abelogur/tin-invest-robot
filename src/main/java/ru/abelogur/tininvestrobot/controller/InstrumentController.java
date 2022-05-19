package ru.abelogur.tininvestrobot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.abelogur.tininvestrobot.domain.CachedInstrument;
import ru.abelogur.tininvestrobot.repository.InstrumentRepository;

import java.util.List;

@RestController
@RequestMapping("instrument")
@RequiredArgsConstructor
public class InstrumentController {

    private final InstrumentRepository instrumentRepository;

    @GetMapping("{figi}")
    public CachedInstrument get(@PathVariable String figi) {
        return instrumentRepository.get(figi);
    }

    @GetMapping
    public List<CachedInstrument> getAllShares() {
        return instrumentRepository.getAllShare();
    }
}
