package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository("FilmDbStorage")
public class FilmDbStorage implements FilmStorage{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film put(Film film) {
        return null;
    }

    @Override
    public Optional<Film> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Optional<Film> removeById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Set<Film> findAll() {
        return null;
    }

    @Override
    public Set<Integer> addLike(Film film, User user) {
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
