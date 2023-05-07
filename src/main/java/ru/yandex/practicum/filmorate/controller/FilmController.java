package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.validation.FilmValidator;
import ru.yandex.practicum.filmorate.controller.validation.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.HashSet;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping
    public HashSet<Film> getAll() {
        return new HashSet<>(films.values());
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        FilmValidator.validate(film);
        if (films.containsKey(film.getId())) {
            throw new ValidationException("this film already exist");
        }
        return putToDatabase(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        FilmValidator.validate(film);
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("this film does not exist");
        }
        return putToDatabase(film);
    }

    private Film putToDatabase(Film film) {
        films.put(film.getId(), film);
        log.info(film.toString());
        return film;
    }
}

