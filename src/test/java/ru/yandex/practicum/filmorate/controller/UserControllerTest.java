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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql("/schema.sql")
@Sql("/data.sql")
class UserControllerTest {
    private final UserController userController;

    private final JdbcTemplate jdbcTemplate;
    User testUser;
    User testBestFriend;
    User testOtherFriend;


    @BeforeEach
    void setUp() {
        String sqlQuery = "delete from film_likes; " +
                "delete from film_genre; " +
                "delete from film; " +
                "delete from user_friends; " +
                "delete from film_user";
        jdbcTemplate.update(sqlQuery);
        testUser = User.builder()
                .email("noKnowledge@java.com")
                .birthday(LocalDate.of(1985, 12, 25))
                .login("elGordoGato")
                .name("John Doe")
                .build();
        userController.create(testUser);
    }

    @Test
    void getAll() {
        assertEquals(List.of(testUser), userController.getAll());
    }

    @Test
    void shouldThrowExceptionWhenCreateSameUser() {
        assertThrows(ValidationException.class, () -> userController.create(testUser),
                "this id already exist");
    }

    @Test
    void shouldThrowExceptionWhenCreateUserWithNullParameters() {
        assertThrows(NullPointerException.class, () -> testUser.toBuilder().login(null).build(),
                "login is absent");
        assertThrows(NullPointerException.class, () -> testUser.toBuilder().email(null).build(),
                "email is absent");
        assertThrows(NullPointerException.class, () -> testUser.toBuilder().birthday(null).build(),
                "birthday is absent");
    }

    @Test
    void shouldCreateUserWithAbsentId() {
        userController.delete(testUser.getId());
        int oldId = testUser.getId();
        testUser.setId(null);
        assertEquals(testUser.toBuilder().id(oldId + 1).build(), userController.create(testUser));
    }

    @Test
    void shouldThrowExceptionWhenCreateUserWithEmptyLogin() {
        User emptyLoginUser = testUser.toBuilder().login("").build();
        assertThrows(ValidationException.class, () -> userController.create(emptyLoginUser),
                "login should not be blank");
        User withSpacesLoginUser = testUser.toBuilder().login("My name is Bob").build();
        assertThrows(ValidationException.class, () -> userController.create(withSpacesLoginUser),
                "login contains spaces");
    }

    @Test
    void shouldThrowExceptionWhenCreateUserWithWrongFormatEmail() {
        User emptyEmailUser = testUser.toBuilder().email("  ").build();
        assertThrows(ValidationException.class, () -> userController.create(emptyEmailUser),
                "email should not be blank");
        User wrongEmailUser = testUser.toBuilder().email("bestEmailAddressEver").build();
        assertThrows(ValidationException.class, () -> userController.create(wrongEmailUser),
                "email should contain @");
    }

    @Test
    void shouldThrowExceptionWhenCreateUserWithBirthdayInFuture() {
        User userFromFuture = testUser.toBuilder().birthday(LocalDate.now().plusDays(1)).build();
        assertThrows(ValidationException.class, () -> userController.create(userFromFuture),
                "hello, guest from future");
    }

    @Test
    void shouldAssignNameToUserWithAbsentName() {
        userController.delete(testUser.getId());
        testUser.setName(null);
        testUser.setId(2);
        assertEquals(testUser.toBuilder().name("elGordoGato").build(), userController.create(testUser));
    }

    @Test
    void update() {
        testUser.setName("Jane Doe");
        userController.update(testUser);
        assertEquals(List.of(testUser), userController.getAll());
    }

    @Test
    void shouldThrowExceptionWhenUpdateUserWithNewId() {
        testUser.setId(999);
        assertThrows(NotFoundException.class, () -> userController.update(testUser),
                "this id does not exist");
    }

    @Test
    void shouldAddFriend() {
        makeFriends();
        userController.makeFriend(testUser.getId(), testBestFriend.getId());
        assertThrows(NotFoundException.class,
                () -> userController.makeFriend(testUser.getId(), testUser.getId() - 10));
        assertEquals(List.of(testBestFriend), userController.findFriends(testUser.getId()));
    }

    @Test
    void shouldRuinFriendship() {
        shouldAddFriend();
        userController.ruinFriendship(testUser.getId(), testBestFriend.getId());
        assertEquals(List.of(), userController.findFriends(testBestFriend.getId()));
        assertThrows(NotFoundException.class,
                () -> userController.ruinFriendship(testOtherFriend.getId(), testUser.getId()));
        assertEquals(List.of(), userController.findFriends(testUser.getId()));
    }

    @Test
    void shouldFindFriends() {
        makeFriends();
        userController.makeFriend(testUser.getId(), testBestFriend.getId());
        userController.makeFriend(testOtherFriend.getId(), testBestFriend.getId());
        assertThrows(NotFoundException.class, () -> userController.findFriends(testUser.getId() + 999));
        assertEquals(List.of(testBestFriend), userController.findFriends(testOtherFriend.getId()));
    }

    @Test
    void shouldFindCommonFriends() {
        shouldFindFriends();
        assertEquals(List.of(testBestFriend),
                userController.findCommonFriends(testUser.getId(), testOtherFriend.getId()));
        assertThrows(NotFoundException.class, () -> userController.findCommonFriends(testUser.getId(), 999));
    }

    private void makeFriends() {
        testBestFriend = User.builder()
                .email("Kortni@kox.com")
                .birthday(LocalDate.of(1985, 12, 25))
                .login("Monica")
                .name("Courtney Kox")
                .build();
        testOtherFriend = User.builder()
                .email("Chendler@Bing.com")
                .birthday(LocalDate.of(1985, 12, 25))
                .login("Chandler")
                .name("Mathew Perry")
                .build();
        userController.create(testBestFriend);
        userController.create(testOtherFriend);
    }


}