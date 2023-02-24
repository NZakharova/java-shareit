package ru.practicum.shareit.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<String> handleNoSuchObject(NoSuchElementException exception) {
        return logWarn(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        return logWarn(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleMethodArgumentNotValid(IllegalArgumentException exception) {
        return logWarn(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorHolder> handleUnsupportedOperation(UnsupportedOperationException exception) {
        log.warn(exception.getMessage() + "; Статус: " + HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(new ErrorHolder(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleThrowable(Throwable throwable) {
        return logError(throwable);
    }

    private ResponseEntity<String> logWarn(String message, HttpStatus status) {
        log.warn(message + "; Статус: " + status);
        return new ResponseEntity<>(message, status);
    }

    private ResponseEntity<String> logError(Throwable throwable) {
        log.error("Ошибка", throwable);
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
