package ru.abelogur.tininvestrobot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.temporal.ChronoUnit;

@Getter
@AllArgsConstructor
public class LoadHistoryIterate {
    private int length;
    private ChronoUnit unit;
}