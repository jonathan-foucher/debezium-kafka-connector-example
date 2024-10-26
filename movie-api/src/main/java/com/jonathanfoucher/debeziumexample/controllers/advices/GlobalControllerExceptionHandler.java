package com.jonathanfoucher.debeziumexample.controllers.advices;

import com.jonathanfoucher.debeziumexample.errors.MovieNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@ControllerAdvice
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(MovieNotFoundException.class)
    public final ResponseEntity<String> handleNotFoundException(Exception exception) {
        log.warn(exception.getMessage());
        return ResponseEntity.status(NOT_FOUND)
                .body(exception.getMessage());
    }
}
