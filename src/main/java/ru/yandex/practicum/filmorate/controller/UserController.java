package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        log.info("Request to get all users");
        Set<User> allUsers = userService.getAll();
        log.info("Found {} users: {}", allUsers.size(), allUsers.stream()
                .map(user -> String.format("User #%s - %s\n", user.getId(), user.getName()))
                .collect(Collectors.toList()));
        return allUsers;
    }

    @GetMapping(value = "/{id}")
    public User getUser(@PathVariable Integer id) {
        log.info("Request to get user with id: {}", id);
        return userService.getById(id);
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Request to create user: {}", user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.info("Request to update user: {}", user);
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("Request to delete user: #{}", id);
        userService.remove(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void makeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Request to make users friends: #{} and #{}", id, friendId);
        userService.makeFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void ruinFriendship(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Request to destroy friendship between users: #{} and #{}", id, friendId);
        userService.destroyFriendship(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findFriends(@PathVariable Integer id) {
        log.info("Request to find friends of user #{}", id);
        List<User> friends = userService.getFriends(id);
        log.info("Found {} friends, Friend's ID: {}", friends.size(),
                friends.stream().map(User::getId).collect(Collectors.toList()));
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> findCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Request to find common friends for user: #{} and #{}", id, otherId);
        Set<User> commonFriends = userService.getCommonFriends(id, otherId);
        log.info("Found {} common friends, Friend's ID: {}", commonFriends.size(),
                commonFriends.stream().map(User::getId).collect(Collectors.toList()));
        return commonFriends;
    }
}
