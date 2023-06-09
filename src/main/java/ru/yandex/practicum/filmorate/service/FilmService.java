package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.validation.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final InMemoryUserStorage userStorage;
    public InMemoryFilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void likeFilm(Integer filmId, Integer userId) {
        Set<Integer> likedUsers = filmStorage.getFilm(filmId).getLikedUsers();
        likedUsers.add(userStorage.getUser(userId).getId());
        filmStorage.getFilm(filmId).setLikedUsers(likedUsers);
    }

    public void removeLike(Integer filmId, Integer userId) {
        if (!filmStorage.getFilm(filmId).getLikedUsers().remove(userId)) {
            throw new NotFoundException("Лайк");
        }
    }

    public List<Film> findTopFilms(Integer count) {
        return filmStorage.getAll().stream()
                .sorted((f0, f1) -> f1.getLikedUsers().size() - f0.getLikedUsers().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
