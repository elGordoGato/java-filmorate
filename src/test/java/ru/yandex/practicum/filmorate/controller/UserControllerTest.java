package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.validation.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
    UserController userController;
    User testUser;


    @BeforeEach
    void setUp() {
        userController = new UserController();
        testUser = User.builder()
                .email("noKnowledge@java.com")
                .birthday(LocalDate.of(1985, 12, 25))
                .login("elGordoGato")
                .name("John Doe")
                .build();
        userController.create(testUser);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAll() {
        assertEquals(Set.of(testUser), userController.getAll());
    }

    @Test
    void shouldThrowExceptionWhenCreateSameUser() {
        assertThrows(ValidationException.class, () -> userController.create(testUser), "this id already exist");
    }

    @Test
    void shouldThrowExceptionWhenCreateUserWithNullParameters() {
        assertThrows(NullPointerException.class, () -> testUser.toBuilder().login(null).build(), "login is absent");
        assertThrows(NullPointerException.class, () -> testUser.toBuilder().email(null).build(), "email is absent");
        assertThrows(NullPointerException.class, () -> testUser.toBuilder().birthday(null).build(), "birthday is absent");
    }

    @Test
    void shouldCreateUserWithAbsentId() {
        int oldId = testUser.getId();
        testUser.setId(null);
        assertEquals(testUser.toBuilder().id(oldId + 1).build(), userController.create(testUser));
    }

    @Test
    void shouldThrowExceptionWhenCreateUserWithEmptyLogin() {
        User emptyLoginUser = testUser.toBuilder().login("").build();
        assertThrows(ValidationException.class, () -> userController.create(emptyLoginUser), "login should not be blank");
        User withSpacesLoginUser = testUser.toBuilder().login("My name is Bob").build();
        assertThrows(ValidationException.class, () -> userController.create(withSpacesLoginUser), "login contains spaces");
    }

    @Test
    void shouldThrowExceptionWhenCreateUserWithWrongFormatEmail() {
        User emptyEmailUser = testUser.toBuilder().email("  ").build();
        assertThrows(ValidationException.class, () -> userController.create(emptyEmailUser), "email should not be blank");
        User wrongEmailUser = testUser.toBuilder().email("bestEmailAddressEver").build();
        assertThrows(ValidationException.class, () -> userController.create(wrongEmailUser), "email should contain @");
    }

    @Test
    void shouldThrowExceptionWhenCreateUserWithBirthdayInFuture() {
        User userFromFuture = testUser.toBuilder().birthday(LocalDate.now().plusDays(1)).build();
        assertThrows(ValidationException.class, () -> userController.create(userFromFuture), "hello, guest from future");
    }

    @Test
    void shouldAssignNameToUserWithAbsentName() {
        testUser.setName(null);
        testUser.setId(2);
        assertEquals(testUser.toBuilder().name("elGordoGato").build(), userController.create(testUser));
    }

    @Test
    void update() {
        testUser.setName("Jane Doe");
        userController.update(testUser);
        assertEquals(Set.of(testUser), userController.getAll());
    }

    @Test
    void shouldThrowExceptionWhenUpdateUserWithNewId() {
        testUser.setId(999);
        assertThrows(ValidationException.class, () -> userController.update(testUser), "this id does not exist");
    }
}