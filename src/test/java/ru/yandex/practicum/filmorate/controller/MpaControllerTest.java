package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql("/schema.sql")
@Sql("/data.sql")
public class MpaControllerTest {
    private final JdbcTemplate jdbcTemplate;
    private final MpaController mpaController;
    List<Mpa> mpaList;

    @BeforeEach
    void setUp() {
        String sqlQuery = "delete from film_likes; " +
                "delete from film_genre; " +
                "delete from film; " +
                "delete from user_friends; " +
                "delete from film_user";
        jdbcTemplate.update(sqlQuery);
        mpaList = List.of(Mpa.builder().id(1).name("G").build(),
                Mpa.builder().id(2).name("PG").build(),
                Mpa.builder().id(3).name("PG-13").build(),
                Mpa.builder().id(4).name("R").build(),
                Mpa.builder().id(5).name("NC-17").build());
    }

    @Test
    void getAll() {
        assertEquals(mpaList, mpaController.getAll());
    }

    @Test
    void getById() {
        assertEquals(mpaList.get(0), mpaController.getById(1));
    }
}
