package ru.yandex.practicum.filmorate.controller.validation;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {
    public static void validate(Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, Film.class + ": name is absent");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, Film.class + ": max description length is 200 symbols");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, Film.class + ": release date should be after 28 December 1895");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, Film.class + ": duration should be positive");
        }
        if (film.getId() == null) {
            film.setId(Film.counter++);
        }
    }
}
