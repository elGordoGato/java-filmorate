package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.validation.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    FilmController filmController;
    Film testFilm;


    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        testFilm = Film.builder()
                .name("How to study Java")
                .releaseDate(LocalDate.of(1985, 12, 25))
                .description("Java for dummies")
                .duration(99999999)
                .build();
        filmController.create(testFilm);
    }

    @Test
    void getAll() {
        assertEquals(Set.of(testFilm), filmController.getAll());
    }

    @Test
    void shouldThrowExceptionWhenCreateSameUser() {
        assertThrows(ValidationException.class, () -> filmController.create(testFilm), "this id already exist");
    }

    @Test
    void shouldThrowExceptionWhenCreateFilmWithNullParameters() {
        assertThrows(NullPointerException.class, () -> testFilm.toBuilder().name(null).build(), "name is absent");
        assertThrows(NullPointerException.class, () -> testFilm.toBuilder().description(null).build(), "description is absent");
        assertThrows(NullPointerException.class, () -> testFilm.toBuilder().releaseDate(null).build(), "release date is absent");
        assertThrows(NullPointerException.class, () -> testFilm.toBuilder().duration(null).build(), "duration is absent");
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
        assertThrows(ValidationException.class, () -> filmController.create(blankNameFilm), "name should not be blank");
    }

    @Test
    void shouldThrowExceptionWhenCreateFilmWithNotPositiveDuration() {
        Film zeroDurationFilm = testFilm.toBuilder().duration(0).build();
        assertThrows(ValidationException.class, () -> filmController.create(zeroDurationFilm), "duration should not be zero");
        Film negativeDuration = testFilm.toBuilder().duration(-1).build();
        assertThrows(ValidationException.class, () -> filmController.create(negativeDuration), "duration should not be negative");
    }

    @Test
    void shouldThrowExceptionWhenCreateFilmWithReleaseDateTooOld() {
        Film tooOldFilm = testFilm.toBuilder().releaseDate(LocalDate.of(1895, 12, 27)).build();
        assertThrows(ValidationException.class, () -> filmController.create(tooOldFilm), "hello, film from past");
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
        assertEquals(Set.of(newFilm), filmController.getAll());
    }

    @Test
    void shouldThrowExceptionWhenUpdateFilmWithNewId() {
        testFilm.setId(999);
        assertThrows(ValidationException.class, () -> filmController.update(testFilm), "this id does not exist");
    }
}