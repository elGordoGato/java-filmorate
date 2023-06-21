package ru.yandex.practicum.filmorate.storage;


import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public Optional<User> putUser(User user) {
        return Optional.ofNullable(users.put(user.getId(), user));
    }

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean removeById(Integer id) {
        return Optional.ofNullable(users.remove(id)).isPresent();
    }

    @Override
    public Set<User> findAll() {
        return new HashSet<>(users.values());
    }

    @Override
    public Integer[] addFriend(User user, User friend) {
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
        return new Integer[]{user.getFriends().size(), friend.getFriends().size()};
    }

    @Override
    public boolean removeFriend(User user, User friend) {
        return (user.getFriends().remove(friend.getId()) && friend.getFriends().remove(user.getId()));
    }


    @Override
    public List<User> findFriends(User user) {
        return user.getFriends().stream().sorted(Integer::compareTo).map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public Set<User> findCommonFriends(User user, User friend) {
        Set<Integer> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(friend.getFriends());
        Arrays.sort(commonFriends.toArray());
        return commonFriends.stream().map(users::get)
                .collect(Collectors.toSet());
    }
}
