package ru.yandex.practicum.filmorate.controller.validation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotFoundException extends RuntimeException {

    public NotFoundException(String item) {
        super(String.format("Данный %s не найден", item));
        log.error(String.format("Данный %s не найден", item));
    }
}
