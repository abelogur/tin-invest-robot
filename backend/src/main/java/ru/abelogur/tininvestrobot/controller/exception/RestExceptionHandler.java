package ru.abelogur.tininvestrobot.controller.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RestRuntimeException.class)
    protected ResponseEntity<?> handleRestRuntimeException(RestRuntimeException e) {
        return new ResponseEntity<>(e.toResponse(), e.getStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleException(Exception e) {
        return new ResponseEntity<>(
                new RestRuntimeExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
