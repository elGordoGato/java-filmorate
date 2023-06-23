package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.controller.validation.NotFoundException;
import ru.yandex.practicum.filmorate.controller.validation.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql("/schema.sql")
@Sql("/data.sql")
class FilmControllerTest {

    private final FilmController filmController;

    private final UserService userService;

    private final JdbcTemplate jdbcTemplate;

    Film testFilm;

    User testUser = User.builder()
            .email("noKnowledge@java.com")
            .birthday(LocalDate.of(1985, 12, 25))
            .login("elGordoGato")
            .name("John Doe")
            .build();


    @BeforeEach
    void setUp() {
        String sqlQuery = "delete from film_likes; " +
                "delete from film_genre; " +
                "delete from film; " +
                "delete from user_friends; " +
                "delete from film_user";
        jdbcTemplate.update(sqlQuery);
        testFilm = Film.builder()
                .name("How to study Java")
                .releaseDate(LocalDate.of(1985, 12, 25))
                .description("Java for dummies")
                .duration(99999999)
                .mpa(Mpa.builder()
                        .id(1)
                        .name("G")
                        .build())
                .genres(List.of(Genre.builder()
                        .id(1)
                        .name("Комедия")
                        .build()))
                .build();
        filmController.create(testFilm);
        userService.create(testUser);
    }

    @Test
    void getAll() {
        assertEquals(List.of(testFilm), filmController.getAll());
    }

    @Test
    void shouldThrowExceptionWhenCreateSameUser() {
        assertThrows(ValidationException.class, () -> filmController.create(testFilm),
                "this id already exist");
    }

    @Test
    void shouldThrowExceptionWhenCreateFilmWithNullParameters() {
        assertThrows(NullPointerException.class, () -> testFilm.toBuilder().name(null).build(),
                "name is absent");
        assertThrows(NullPointerException.class, () -> testFilm.toBuilder().description(null).build(),
                "description is absent");
        assertThrows(NullPointerException.class, () -> testFilm.toBuilder().releaseDate(null).build(),
                "release date is absent");
        assertThrows(NullPointerException.class, () -> testFilm.toBuilder().duration(null).build(),
                "duration is absent");
    }

    @Test
    void shouldCreateFilmWithAbsentId() {
        int oldId = testFilm.getId();
        testFilm.setId(null);
        assertEquals(testFilm.toBuilder().id(oldId + 1).build(), filmController.create(testFilm));
    }

    @Test
    void shouldThrowExceptionWhenCreateFilmWithEmptyName() {
        Film blankNameFilm = testFilm.toBuilder().name("  ").build();
        assertThrows(ValidationException.class, () -> filmController.create(blankNameFilm),
                "name should not be blank");
    }

    @Test
    void shouldThrowExceptionWhenCreateFilmWithNotPositiveDuration() {
        Film zeroDurationFilm = testFilm.toBuilder().duration(0).build();
        assertThrows(ValidationException.class, () -> filmController.create(zeroDurationFilm),
                "duration should not be zero");
        Film negativeDuration = testFilm.toBuilder().duration(-1).build();
        assertThrows(ValidationException.class, () -> filmController.create(negativeDuration),
                "duration should not be negative");
    }

    @Test
    void shouldThrowExceptionWhenCreateFilmWithReleaseDateTooOld() {
        Film tooOldFilm = testFilm.toBuilder().releaseDate(LocalDate.of(1895, 12, 27)).build();
        assertThrows(ValidationException.class, () -> filmController.create(tooOldFilm),
                "hello, film from past");
    }

    @Test
    void shouldThrowExceptionWhenCreateFilmWithTooLongDescription() {
        Film tooLongDescriptionFilm = testFilm.toBuilder().description("a".repeat(201)).build();
        assertThrows(ValidationException.class, () -> filmController.create(tooLongDescriptionFilm),
                "description should be less than 200 symbols, instead: " + testFilm.getDescription().length());
    }


    @Test
    void update() {
        Film newFilm = testFilm.toBuilder().name("New name").build();
        filmController.update(newFilm);
        assertEquals(List.of(newFilm), filmController.getAll());
    }

    @Test
    void shouldThrowExceptionWhenUpdateFilmWithNewId() {
        testFilm.setId(999);
        assertThrows(NotFoundException.class, () -> filmController.update(testFilm), "this id does not exist");
    }

    @Test
    void shouldLikeFilm() {
        filmController.like(testFilm.getId(), testUser.getId());
        assertEquals(List.of(testUser.getId()), filmController.getLikes(testFilm.getId()));
        assertThrows(NotFoundException.class, () -> filmController.like(testFilm.getId(), testUser.getId() + 1),
                "this user does not exist");
        assertThrows(NotFoundException.class, () -> filmController.like(testFilm.getId() + 1, testUser.getId()),
                "this film does not exist");
    }

    @Test
    void shouldUnlikeFilm() {
        shouldLikeFilm();
        filmController.unlike(testFilm.getId(), testUser.getId());
        assertEquals(List.of(), filmController.getLikes(testFilm.getId()));
        assertThrows(NotFoundException.class, () -> filmController.unlike(testFilm.getId(), testUser.getId() + 1),
                "this user does not exist");
        assertThrows(NotFoundException.class, () -> filmController.unlike(testFilm.getId() + 1, testUser.getId()),
                "this film does not exist");
    }

    @Test
    void shouldFindTopFilms() {
        filmController.like(testFilm.getId(), testUser.getId());
        Film testFilm2 = Film.builder()
                .name("How to study Java: 2 - Revenge")
                .releaseDate(LocalDate.of(1985, 12, 26))
                .description("Java for dummies: revenge")
                .duration(99999999)
                .mpa(Mpa.builder()
                        .id(2)
                        .name("PG")
                        .build())
                .genres(List.of(Genre.builder()
                        .id(1)
                        .name("Комедия")
                        .build()))
                .build();
        filmController.create(testFilm2);
        assertEquals(List.of(testFilm, testFilm2), filmController.findTop(3));
        assertEquals(List.of(testFilm), filmController.findTop(1));
    }

}