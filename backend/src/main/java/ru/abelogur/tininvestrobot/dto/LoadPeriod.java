package ru.abelogur.tininvestrobot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class LoadPeriod {
    private Instant from;
    private Instant to;
}