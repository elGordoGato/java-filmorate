package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Optional<Film> add(Film film);

    Optional<Film> findById(Integer id);

    Optional<Film> update(Film film);

    boolean removeById(Integer id);


    List<Film> findAll();

    boolean addLike(Film film, User user);

    List<Integer> findLikes(Integer id);

    boolean removeLike(Film film, User user);

    List<Film> findTop(Integer count);
}
