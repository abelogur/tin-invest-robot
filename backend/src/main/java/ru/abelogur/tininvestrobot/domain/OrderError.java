package ru.abelogur.tininvestrobot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class OrderError {
    private OrderReason reason;
    private String errorMessage;
    private String errorCode;
    private Instant instant;
}
