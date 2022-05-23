package ru.abelogur.tininvestrobot.controller.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RestRuntimeException extends RuntimeException {
    private final String message;
    private final HttpStatus status;

    public RestRuntimeException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }

    public RestRuntimeExceptionResponse toResponse() {
        return new RestRuntimeExceptionResponse(message, status.toString());
    }
}
