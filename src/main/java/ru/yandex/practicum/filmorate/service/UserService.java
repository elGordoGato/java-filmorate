package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.validation.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {


    public InMemoryUserStorage userStorage;


    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        User friend = userStorage.getUser(friendId);
        userStorage.getUser(userId).getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        if (!getFriends(userId).contains(userStorage.getUser(friendId)) || !getFriends(friendId).contains(userStorage.getUser(userId))) {
            throw new NotFoundException("Friend " + userId + " or " + friendId);
        }
        userStorage.getUser(userId).getFriends().remove(friendId);
        userStorage.getUser(friendId).getFriends().remove(userId);
    }

    public List<User> getFriends(Integer userId) {
        Set<Integer> friendsId = new HashSet<>(userStorage.getUser(userId).getFriends());
        return friendsId.stream().sorted(Integer::compareTo).map(id -> userStorage.getUser(id))
                .collect(Collectors.toList());
    }

    public Set<User> getCommonFriends(Integer userId, Integer friendId) {
        Set<Integer> commonFriends = new HashSet<>(userStorage.getUser(userId).getFriends());
        commonFriends.retainAll(userStorage.getUser(friendId).getFriends());
        Arrays.sort(commonFriends.toArray());
        return commonFriends.stream().map(id -> userStorage.getUser(id))
                .collect(Collectors.toSet());
    }
}
