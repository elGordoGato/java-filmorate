package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository("FilmDbStorage")
public class FilmDbStorage implements FilmStorage{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film film) {
        String sqlQuery = "insert into film(id, name, description, release_date, duration, rating_id) " +
                "values (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                film.getId(),
                film.getName(),
        film.getDescription(),
        film.getReleaseDate(),
        film.getDuration(),
        film.getMpa());
        return findById(film.getId()).orElseThrow();
    }

    @Override
    public Optional<Film> findById(Integer id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film where id = ?", id);
        Film film = null;
        if(filmRows.next()) {
            film = Film.builder()
                    .id(filmRows.getInt("id"))
                    .name(Objects.requireNonNull(filmRows.getString("name")))
                    .description(Objects.requireNonNull(filmRows.getString("description")))
                    .releaseDate(Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .mpa(filmRows.getInt("rating_id"))
                    .build();
        }
        return Optional.ofNullable(film);
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update film set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa(),
                film.getId());
        return findById(film.getId()).orElseThrow();
    }

    @Override
    public boolean removeById(Integer id) {
        String sqlQuery = "delete from film where id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public List<Film> findAll() {
        String sql = "select * from film";
        return
                jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getInt("id"))
                .name(Objects.requireNonNull(rs.getString("name")))
                .description(Objects.requireNonNull(rs.getString("description")))
                .releaseDate(Objects.requireNonNull(rs.getDate("release_date")).toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(rs.getObject("rating_id", Mpa.class))
                .build();
    }


    @Override
    public boolean addLike(Film film, User user) {
        String sqlQuery = "insert into film_likes (film_id, liked_user_id) " +
                "values (?, ?)";
        return jdbcTemplate.update(sqlQuery,
                film.getId(),
                user.getId())>0;
    }

    @Override
    public List<Integer> findLikes(Integer id) {
        String sql = "select liked_user_id from film_likes WHERE film_id = ?";
        return
                jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("liked_user_id"), id);
    }

    @Override
    public boolean removeLike(Film film, User user) {
        String sqlQuery = "delete from film_likes where film_id = ? AND liked_user_id = ?";
        return jdbcTemplate.update(sqlQuery, film.getId(), user.getId()) > 0;
    }

    @Override
    public List<Film> findTop(Integer count) {
        String sql = "select * from film " +
                "WHERE id IN (SELECT film_id " +
                            "FROM film_likes " +
                            "GROUP BY film_id " +
                            "ORDER BY COUNT(liked_user_id) DESC " +
                            "LIMIT ?";
        return
                jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }
}
