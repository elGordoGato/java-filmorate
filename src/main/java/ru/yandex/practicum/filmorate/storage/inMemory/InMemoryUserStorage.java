package ru.yandex.practicum.filmorate.storage.inMemory;


import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public Optional<User> addUser(User user) {
        return Optional.ofNullable(users.put(user.getId(), user));
    }

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> updateUser(User user) {
        return Optional.empty();
    }

    @Override
    public boolean removeById(Integer id) {
        return Optional.ofNullable(users.remove(id)).isPresent();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Integer[] addFriend(User user, User friend) {

        return null;
    }

    @Override
    public boolean removeFriend(User user, User friend) {
        return false;
    }


    @Override
    public List<User> findFriends(User user) {
        return null;
    }

    @Override
    public List<User> findCommonFriends(User user, User friend) {
        return null;
    }
}
