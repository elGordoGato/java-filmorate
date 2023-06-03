package ru.yandex.practicum.filmorate.controller.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class ValidationException extends RuntimeException {
    final String message;
    final HttpStatus status;

    public ValidationException(HttpStatus status, String message) {
        log.error(message);
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
