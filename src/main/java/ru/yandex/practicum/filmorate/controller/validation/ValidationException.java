package ru.yandex.practicum.filmorate.controller.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class ValidationException extends RuntimeException {
    final String message;
    final HttpStatus STATUS;

    public ValidationException(HttpStatus STATUS, String message) {
        log.error(message);
        this.STATUS = STATUS;
        this.message = message;
    }

    public HttpStatus getSTATUS() {
        return STATUS;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
