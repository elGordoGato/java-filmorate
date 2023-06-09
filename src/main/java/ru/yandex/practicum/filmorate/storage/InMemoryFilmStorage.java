package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.validation.NotFoundException;
import ru.yandex.practicum.filmorate.controller.validation.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final String FILM = "Фильм";
    protected final HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        if (films.containsKey(film.getId())) {
            throw new ValidationException("Этот фильм уже существует");
        }
        if (film.getLikedUsers() == null) {
            film.setLikedUsers(new HashSet<>());
        }
        films.put(film.getId(), film);
        return getFilm(film.getId());
    }

    @Override
    public Film getFilm(Integer id) {
        if (films.get(id) == null) {
            throw new NotFoundException(FILM);
        }
        return films.get(id);
    }

    @Override
    public void removeFilm(Film film) {
        Optional<Film> deletedFIlm = Optional.ofNullable(films.remove(film.getId()));
        if (deletedFIlm.isEmpty()) {
            throw new NotFoundException(FILM);
        }
    }

    @Override
    public void updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException(FILM);
        }
        if (film.getLikedUsers() == null) {
            film.setLikedUsers(new HashSet<>());
        }
        films.put(film.getId(), film);
    }

    @Override
    public HashSet<Film> getAll() {
        return new HashSet<>(films.values());
    }
}
