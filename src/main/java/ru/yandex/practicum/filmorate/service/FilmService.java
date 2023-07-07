package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.validation.FilmValidator;
import ru.yandex.practicum.filmorate.controller.validation.NotFoundException;
import ru.yandex.practicum.filmorate.controller.validation.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FilmService {

    private static final String FILM = "Фильм";

    private final UserService userService;

    private final FilmStorage filmStorage;

    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       UserService userService, GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.genreStorage = genreStorage;
    }

    public Film create(Film film) {
        FilmValidator.validate(film);
        if (filmStorage.findById(film.getId()).isPresent()) {
            throw new ValidationException("Этот фильм уже существует");
        }
        Film createdFilm = filmStorage.add(film).orElseThrow(() -> new NotFoundException(FILM + film.getId()));
        log.info("Film created: {}", createdFilm);
        return createdFilm;
    }

    public List<Film> getAll() {
        return setGenres(filmStorage.findAll());
    }

    public Film getById(Integer id) {
        return filmStorage.findById(id).orElseThrow(() -> new NotFoundException(FILM + id));
    }

    public Film update(Film film) {
        FilmValidator.validate(film);
        Film updatedFilm = filmStorage.update(film)
                .orElseThrow(() -> new NotFoundException(FILM + film.getId()));
        log.info("Film updated\nNew value: {}", updatedFilm);
        return updatedFilm;
    }

    public void removeById(Integer id) {
        if (filmStorage.removeById(id)) {
            log.info("Film: {} - deleted", id);
        } else {
            throw new NotFoundException(FILM + id);
        }
    }

    public void likeFilm(Integer filmId, Integer userId) {
        if (filmStorage.addLike(getById(filmId), userService.getById(userId))) {
            log.info("Film {} was liked by user: {}", filmId, userId);
        }
    }

    public void deleteLike(Integer filmId, Integer userId) {
        if (!filmStorage.removeLike(getById(filmId), userService.getById(userId))) {
            throw new NotFoundException("Лайк от пользователя " + userId);
        }
        log.info("Film #{} - Like from user #{} deleted", filmId, userId);
    }

    public List<Film> getTop(Integer count) {
        List<Film> topFilms = setGenres(filmStorage.findTop(count));
        log.info("Top {} IMDB: {}", count, topFilms);
        return topFilms;
    }


    public List<Integer> getLikes(Integer id) {
        return filmStorage.findLikes(id);
    }

    private List<Film> setGenres(List<Film> films) {
        Map<Integer, List<Genre>> allGenres = genreStorage.findAllFilmGenres();
        for (Film film : films) {
            film.setGenres(allGenres.getOrDefault(film.getId(), new ArrayList<>()));
        }
        return films;
    }
}
