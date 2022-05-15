package ru.abelogur.tininvestrobot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class OrderMetadata {
    private String reason;
    private BigDecimal price;
    private Instant time;
}
