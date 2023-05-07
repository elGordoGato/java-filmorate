package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.validation.UserValidator;
import ru.yandex.practicum.filmorate.controller.validation.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.HashSet;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping
    public HashSet<User> getAll() {
        return new HashSet<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody User user) {
        UserValidator.validate(user);
        if (users.containsKey(user.getId())) {
            throw new ValidationException("This user already exist");
        }
        return putToDatabase(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        UserValidator.validate(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("This user does not exist");
        }
        return putToDatabase(user);
    }


    private User putToDatabase(@RequestBody User user) {
        users.put(user.getId(), user);
        log.info(user.toString());
        return user;
    }
}
