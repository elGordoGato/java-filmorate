package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;

public interface FilmStorage {
    Film addFilm(Film film);

    Film getFilm(Integer id);

    void removeFilm(Film film);

    void updateFilm(Film film);

    HashSet<Film> getAll();
}
