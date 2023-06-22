package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public List<Film> getAll() {
        log.info("Request to get all films");
        List<Film> allFilms = filmService.getAll();
        log.info("Found {} films: {}", allFilms.size(), allFilms.stream()
                .map(film -> String.format("Film #%s - %s\n", film.getId(), film.getName()))
                .collect(Collectors.toList()));
        return allFilms;
    }

    @GetMapping(value = "/{id}")
    public Film getById(@PathVariable Integer id) {
        log.info("Request to get film with id: {}", id);
        return filmService.getById(id);
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Request to create film: {}", film);
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("Request to update film: {}", film);
        return filmService.update(film);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("Request to delete film: #{}", id);
        filmService.removeById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Request to like film #{} from user #{}", id, userId);
        filmService.likeFilm(id, userId);
    }

    @GetMapping("/{id}/like")
    public List<Integer> getLikes(@PathVariable Integer id) {
        log.info("Request to get likes for film #{}", id);
        return filmService.getLikes(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Request to unlike film #{} from user #{}", id, userId);
        filmService.deleteLike(id, userId);
    }


    @GetMapping("/popular")
    public List<Film> findTop(@RequestParam(value = "count", defaultValue = "10") Integer count) {
        log.info("Request to find best {} films", count);
        return filmService.getTop(count);
    }

}

