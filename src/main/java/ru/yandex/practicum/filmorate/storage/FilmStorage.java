package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Film put(Film film);

    Optional<Film> findById(Integer id);


    Optional<Film> removeById(Integer id);


    Set<Film> findAll();

    Set<Integer> addLike(Film film, User user);

    boolean removeLike(Film film, User user);

    List<Film> findTop(Integer count);
}
