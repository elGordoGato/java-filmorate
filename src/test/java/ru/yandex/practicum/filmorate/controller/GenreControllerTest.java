package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql("/schema.sql")
@Sql("/data.sql")
public class GenreControllerTest {
    private final JdbcTemplate jdbcTemplate;
    private final GenreController genreController;
    List<Genre> testGenres;

    @BeforeEach
    void setUp() {
        String sqlQuery = "delete from film_likes; " +
                "delete from film_genre; " +
                "delete from film; " +
                "delete from user_friends; " +
                "delete from film_user";
        jdbcTemplate.update(sqlQuery);
        testGenres = List.of(Genre.builder().id(1).name("Комедия").build(),
                Genre.builder().id(2).name("Драма").build(),
                Genre.builder().id(3).name("Мультфильм").build(),
                Genre.builder().id(4).name("Триллер").build(),
                Genre.builder().id(5).name("Документальный").build(),
                Genre.builder().id(6).name("Боевик").build());
    }

    @Test
    void getAll() {
        assertEquals(testGenres, genreController.getAll());
    }

    @Test
    void getById() {
        assertEquals(testGenres.get(0), genreController.getById(1));
    }
}
