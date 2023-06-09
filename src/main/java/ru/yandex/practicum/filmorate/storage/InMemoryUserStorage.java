package ru.yandex.practicum.filmorate.storage;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.validation.NotFoundException;
import ru.yandex.practicum.filmorate.controller.validation.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.HashSet;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final static String USER = "Пользователь #";
    private static Integer counter = 1;
    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        if (users.containsKey(user.getId())) {
            throw new ValidationException("Данный пользователь уже существует");
        }
        for (User storedUser : users.values()) {
            if (storedUser.getLogin().equals(user.getLogin()) || storedUser.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Данный пользователь уже существует");
            }
        }
        if (user.getId() == null) {
            user.setId(counter++);
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User getUser(Integer id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(USER + id);
        }
        return users.get(id);
    }

    @Override
    public void removeUser(User user) {
        if (users.remove(user.getId()) == null) {
            throw new NotFoundException(USER + user.getId());
        }
    }

    @Override
    public void updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException(USER + user.getId());
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        users.put(user.getId(), user);
    }

    @Override
    public HashSet<User> getAll() {
        return new HashSet<>(users.values());
    }
}
