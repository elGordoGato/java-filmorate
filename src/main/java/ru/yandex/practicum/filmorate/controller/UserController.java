package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.validation.UserValidator;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {


    UserService userService;

    @Autowired
    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Set<User> getAll() {
        log.info(userService.userStorage.getAll().toString());
        return userService.userStorage.getAll();
    }

    @GetMapping(value = "/{id}")
    public User getUser(@PathVariable Integer id) {
        log.info(userService.userStorage.getUser(id).toString());
        return userService.userStorage.getUser(id);
    }

    @PostMapping
    public User create(@RequestBody User user) {
        UserValidator.validate(user);
        User newUser = userService.userStorage.addUser(user);
        log.info(newUser.toString());
        return newUser;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        UserValidator.validate(user);
        userService.userStorage.updateUser(user);
        log.info(userService.userStorage.getUser(user.getId()).toString());
        return userService.userStorage.getUser(user.getId());
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void makeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.addFriend(id, friendId);
        log.info(userService.getFriends(id).toString());
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.removeFriend(id, friendId);
        log.info(userService.getFriends(id).toString());
    }

    @GetMapping("/{id}/friends")
    public List<User> findFriends(@PathVariable Integer id) {
        log.info(userService.getFriends(id).toString());
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> findFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info(userService.getCommonFriends(id, otherId).toString());
        return userService.getCommonFriends(id, otherId);
    }
}
