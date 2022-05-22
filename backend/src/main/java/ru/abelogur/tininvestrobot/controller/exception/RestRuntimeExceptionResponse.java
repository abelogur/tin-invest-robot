package ru.abelogur.tininvestrobot.controller.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RestRuntimeExceptionResponse {
    private final String status;
    private final String message;
}
