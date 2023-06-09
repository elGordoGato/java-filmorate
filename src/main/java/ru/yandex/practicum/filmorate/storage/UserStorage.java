package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;

public interface UserStorage {
    User addUser(User user);

    User getUser(Integer id);

    void removeUser(User user);

    void updateUser(User user);

    HashSet<User> getAll();
}
