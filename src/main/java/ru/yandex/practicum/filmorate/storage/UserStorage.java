package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> addUser(User user);

    Optional<User> findById(Integer id);

    Optional<User> updateUser(User user);

    boolean removeById(Integer id);

    List<User> findAll();

    Integer[] addFriend(User user, User friend);

    boolean removeFriend(User user, User friend);

    List<User> findFriends(User user);

    List<User> findCommonFriends(User user, User friend);
}
