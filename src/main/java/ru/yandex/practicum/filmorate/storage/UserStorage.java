package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    Optional<User> putUser(User user);

    Optional<User> findById(Integer id);

   boolean removeById(Integer id);

    Set<User> findAll();

    Integer[] addFriend(User user, User friend);

    boolean removeFriend(User user, User friend);

    List<User> findFriends(User user);

    Set<User> findCommonFriends(User user, User friend);
}
