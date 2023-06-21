package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public Film put(Film film) {
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public Optional<Film> findById(Integer id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Optional<Film> removeById(Integer id) {
        return Optional.ofNullable(films.remove(id));
    }

    @Override
    public Set<Film> findAll() {
        return new HashSet<>(films.values());
    }

    @Override
    public Set<Integer> addLike(Film film, User user) {
        film.getLikedUsers().add(user.getId());
        return film.getLikedUsers();
    }

    @Override
    public boolean removeLike(Film film, User user) {
        return film.getLikedUsers().remove(user.getId());
    }

    @Override
    public List<Film> findTop(Integer count) {
        return findAll().stream()
                .sorted((f0, f1) -> f1.getLikedUsers().size() - f0.getLikedUsers().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
