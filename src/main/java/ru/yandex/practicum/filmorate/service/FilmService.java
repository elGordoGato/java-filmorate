package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.validation.FilmValidator;
import ru.yandex.practicum.filmorate.controller.validation.NotFoundException;
import ru.yandex.practicum.filmorate.controller.validation.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {

    private static final String FILM = "Фильм";
    private static Integer counter = 1;

    private final UserService userService;

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film create(Film film) {
        FilmValidator.validate(film);
        if (filmStorage.findById(film.getId()).isPresent()) {
            throw new ValidationException("Этот фильм уже существует");
        }
        if (film.getId() == null) {
            film.setId(counter++);
        } else if (counter < film.getId()) {
            counter = film.getId();
        }
        log.info("Film created: {}", film);
        return filmStorage.add(film);
    }

    public List<Film> getAll() {
        return filmStorage.findAll();
    }

    public Film getById(Integer id) {
        Optional<Film> film = filmStorage.findById(id);
        if (film.isEmpty()) {
            throw new NotFoundException(FILM + id);
        }
        log.info("Film found: {}", film.get());
        return film.get();
    }

    public Film update(Film film) {
        FilmValidator.validate(film);
        if (filmStorage.findById(film.getId()).isEmpty()) {
            throw new NotFoundException(FILM + film.getId());
        }
        log.info("Film updated\nNew value: {}", filmStorage.update(film));
        return filmStorage.findById(film.getId()).get();
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
        List<Film> topFilms = filmStorage.findTop(count);
        log.info("Top {} IMDB: {}", count, topFilms);
        return topFilms;
    }

    public List<Integer> getLikes(Integer id) {
        return filmStorage.findLikes(id);
    }
}
