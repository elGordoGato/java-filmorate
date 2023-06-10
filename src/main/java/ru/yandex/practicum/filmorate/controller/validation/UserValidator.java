package ru.yandex.practicum.filmorate.controller.validation;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidator {
    public static void validate(User user) throws ValidationException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException(User.class + ": Email is absent or wrong format");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException(User.class + ": Login is absent or contains spaces");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException(User.class + ": Birthday can not be in future");
        }
    }
}
