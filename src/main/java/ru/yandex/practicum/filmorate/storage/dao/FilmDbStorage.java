package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

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
                film.getMpa().getId());
        updateGenres(film);
        return findById(film.getId()).orElseThrow();
    }

    private void updateGenres(Film film) {
        String sqlQuery = "insert into film_genre(film_id, genre_id) " +
                "values (?, ?)";
        if (film.getGenres() != null && film.getGenres().size() > 0) {
            film.getGenres().forEach(genre -> jdbcTemplate.update(sqlQuery, film.getId(), genre.getId()));
        }
    }

    @Override
    public Optional<Film> findById(Integer id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT f.*, m.name \"mpa_name\" " +
                "FROM film AS f " +
                "LEFT JOIN film_rating AS m ON f.rating_id = m.id " +
                "WHERE f.id = ?", id);
        Film film = null;
        if (filmRows.next()) {
            film = Film.builder()
                    .id(filmRows.getInt("id"))
                    .name(Objects.requireNonNull(filmRows.getString("name")))
                    .description(Objects.requireNonNull(filmRows.getString("description")))
                    .releaseDate(Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .mpa(Mpa.builder()
                            .id(filmRows.getInt("rating_id"))
                            .name(filmRows.getString("mpa_name"))
                            .build())
                    .genres(findGenres(id))
                    .build();
        }
        return Optional.ofNullable(film);
    }

    private List<Genre> findGenres(Integer id) {
        String sql = "SELECT * FROM genre WHERE id IN (SELECT genre_id FROM film_genre WHERE film_id = ?)";
        return
                jdbcTemplate.query(sql, (rs, rowNum) -> Genre.builder()
                                .id(rs.getInt("id"))
                                .name(rs.getString("name"))
                                .build(),
                        id);
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update film set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "where id = ?;" +
                "delete from film_genre where film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId(),
                film.getId());
        updateGenres(film);
        return findById(film.getId()).orElseThrow();
    }

    @Override
    public boolean removeById(Integer id) {
        String sqlQuery = "delete from film where id = ?;" +
                "delete from film_genre where film_id = ?";
        return jdbcTemplate.update(sqlQuery, id, id) > 0;
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT f.*, m.name \"mpa_name\" " +
                "FROM film AS f " +
                "LEFT JOIN film_rating AS m ON f.rating_id = m.id ";
        return
                jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        return Film.builder()
                .id(id)
                .name(Objects.requireNonNull(rs.getString("name")))
                .description(Objects.requireNonNull(rs.getString("description")))
                .releaseDate(Objects.requireNonNull(rs.getDate("release_date")).toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(Mpa.builder()
                        .id(rs.getInt("rating_id"))
                        .name(rs.getString("mpa_name"))
                        .build())
                .genres(findGenres(id))
                .build();
    }


    @Override
    public boolean addLike(Film film, User user) {
        String sqlQuery = "insert into film_likes (film_id, liked_user_id) " +
                "values (?, ?)";
        return jdbcTemplate.update(sqlQuery,
                film.getId(),
                user.getId()) > 0;
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
        String sql = "SELECT f.*, m.name \"mpa_name\" " +
                "FROM film AS f " +
                "LEFT JOIN film_rating AS m ON f.rating_id = m.id " +
                "LEFT JOIN film_likes AS fl ON f.id = fl.film_id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.liked_user_id) DESC " +
                "LIMIT ?";
        return
                jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }
}
