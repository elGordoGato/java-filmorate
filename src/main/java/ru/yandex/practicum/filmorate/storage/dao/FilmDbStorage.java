package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Film> add(Film film) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", film.getReleaseDate());
        parameters.put("duration", film.getDuration());
        parameters.put("rating_id", film.getMpa().getId());
        Number key = insert.executeAndReturnKey(parameters);
        film.setId(key.intValue());
        updateGenres(film);
        return findById(film.getId());
    }

    private void updateGenres(Film film) throws DataIntegrityViolationException {
        String sqlQuery = "INSERT INTO film_genre(film_id, genre_id) " +
                "VALUES (?, ?)";
        if (film.getGenres() != null && film.getGenres().size() > 0) {
            List<Object[]> params = film.getGenres().stream()
                    .map(genre -> new Object[]{film.getId(), genre.getId()}).collect(Collectors.toList());
            jdbcTemplate.batchUpdate(sqlQuery, params);
        }
    }

    @Override
    public Optional<Film> findById(Integer id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT f.*, m.name \"mpa\" " +
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
                            .name(filmRows.getString("mpa"))
                            .build())
                    .genres(findGenresById(id))
                    .build();
        }
        return Optional.ofNullable(film);
    }

    private List<Genre> findGenresById(Integer id) {
        String sql = "SELECT * FROM genre WHERE id IN (SELECT genre_id FROM film_genre WHERE film_id = ?)";
        return
                jdbcTemplate.query(sql, (rs, rowNum) -> Genre.builder()
                                .id(rs.getInt("id"))
                                .name(rs.getString("name"))
                                .build(),
                        id);
    }

    @Override
    public Optional<Film> update(Film film) {
        String sqlQuery = "UPDATE film SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "WHERE id = ?;" +
                "DELETE FROM film_genre WHERE film_id = ?";
        try {
            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId(),
                    film.getId());
            updateGenres(film);
        } catch (DataIntegrityViolationException e) {
            return Optional.empty();
        }
        return findById(film.getId());
    }

    @Override
    public boolean removeById(Integer id) {
        String sqlQuery = "DELETE FROM film WHERE id = ?;" +
                "DELETE FROM film_genre WHERE film_id = ?";
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
                .build();
    }


    @Override
    public boolean addLike(Film film, User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film_likes")
                .usingColumns("film_id", "liked_user_id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("film_id", film.getId());
        parameters.put("liked_user_id", user.getId());
        int rowsAffected = simpleJdbcInsert.execute(parameters);
        return rowsAffected > 0;
    }

    @Override
    public List<Integer> findLikes(Integer id) {
        String sql = "SELECT liked_user_id FROM film_likes WHERE film_id = ?";
        return
                jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("liked_user_id"), id);
    }

    @Override
    public boolean removeLike(Film film, User user) {
        String sqlQuery = "DELETE FROM film_likes WHERE film_id = ? AND liked_user_id = ?";
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
  
    private void emptyMethod(){}

}
