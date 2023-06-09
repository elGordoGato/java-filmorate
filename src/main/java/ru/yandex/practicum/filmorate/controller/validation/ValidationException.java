package ru.yandex.practicum.filmorate.controller.validation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationException extends RuntimeException {
    final String message;

    public ValidationException(String message) {
        log.error(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
