package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.validation.FilmValidator;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {


    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Set<Film> getAllFilms() {
        return filmService.filmStorage.getAll();
    }

    @GetMapping(value = "/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        return filmService.filmStorage.getFilm(id);
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        FilmValidator.validate(film);
        log.info(film.toString());
        return filmService.filmStorage.addFilm(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        FilmValidator.validate(film);
        log.info(film.toString());
        filmService.filmStorage.updateFilm(film);
        return filmService.filmStorage.getFilm(film.getId());
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.likeFilm(id, userId);
        log.info(filmService.filmStorage.getFilm(id).getLikedUsers().toString());
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.removeLike(id, userId);
        log.info(filmService.filmStorage.getFilm(id).getLikedUsers().toString());
    }


    @GetMapping("/popular")
    public List<Film> findTopFilms(@RequestParam(value = "count", defaultValue = "10") Integer count) {
        filmService.findTopFilms(count);
        log.info(filmService.findTopFilms(count).toString());
        return filmService.findTopFilms(count);
    }

}

