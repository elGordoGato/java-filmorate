package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GenreStorage {
    Optional<Genre> findById(Integer id);

    List<Genre> findAll();

    Map<Integer, List<Genre>> findAllFilmGenres();
}
