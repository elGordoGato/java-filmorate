package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Repository
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public Film add(Film film) {
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public Optional<Film> findById(Integer id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public boolean removeById(Integer id) {
        return Optional.ofNullable(films.remove(id)).isPresent();
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public boolean addLike(Film film, User user) {
        return false;
    }

    @Override
    public List<Integer> findLikes(Integer id) {
        return null;
    }

    @Override
    public boolean removeLike(Film film, User user) {
        return false;
    }

    @Override
    public List<Film> findTop(Integer count) {
        return null;
    }
}
