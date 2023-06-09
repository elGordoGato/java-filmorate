package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.validation.NotFoundException;
import ru.yandex.practicum.filmorate.controller.validation.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    FilmController filmController;
    Film testFilm;

    InMemoryUserStorage userStorage = new InMemoryUserStorage();
    User testUser = User.builder()
            .email("noKnowledge@java.com")
            .birthday(LocalDate.of(1985, 12, 25))
            .login("elGordoGato")
            .name("John Doe")
            .build();


    @BeforeEach
    void setUp() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), userStorage));
        testFilm = Film.builder()
                .name("How to study Java")
                .releaseDate(LocalDate.of(1985, 12, 25))
                .description("Java for dummies")
                .duration(99999999)
                .build();
        filmController.create(testFilm);
        userStorage.addUser(testUser);
    }

    @Test
    void getAll() {
        assertEquals(Set.of(testFilm), filmController.getAllFilms());
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
        assertEquals(Set.of(newFilm), filmController.getAllFilms());
    }

    @Test
    void shouldThrowExceptionWhenUpdateFilmWithNewId() {
        testFilm.setId(999);
        assertThrows(NotFoundException.class, () -> filmController.update(testFilm), "this id does not exist");
    }

    @Test
    void shouldLikeFilm() {
        filmController.like(testFilm.getId(), testUser.getId());
        assertEquals(Set.of(testUser.getId()), testFilm.getLikedUsers());
        assertThrows(NotFoundException.class, () -> filmController.like(testFilm.getId(), testUser.getId() + 1),
                "this user does not exist");
        assertThrows(NotFoundException.class, () -> filmController.like(testFilm.getId() + 1, testUser.getId()),
                "this film does not exist");
    }

    @Test
    void shouldUnlikeFilm() {
        shouldLikeFilm();
        filmController.unlike(testFilm.getId(), testUser.getId());
        assertEquals(Set.of(), testFilm.getLikedUsers());
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
                .build();
        filmController.create(testFilm2);
        assertEquals(List.of(testFilm, testFilm2), filmController.findTopFilms(3));
        assertEquals(List.of(testFilm), filmController.findTopFilms(1));
    }

}