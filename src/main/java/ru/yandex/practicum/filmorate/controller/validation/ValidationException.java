package ru.yandex.practicum.filmorate.controller.validation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        log.error(message);
    }
}
