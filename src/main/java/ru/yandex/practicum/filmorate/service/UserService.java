package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.validation.NotFoundException;
import ru.yandex.practicum.filmorate.controller.validation.UserValidator;
import ru.yandex.practicum.filmorate.controller.validation.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private static final String USER = "Пользователь #";

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }


    public User getById(Integer id) {
        User user = userStorage.findById(id).orElseThrow(() -> new NotFoundException(USER + id));
        log.info("User found: {}", user);
        return user;
    }

    public List<User> getAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        UserValidator.validate(user);
        if (userStorage.findAll().contains(user)) {
            throw new ValidationException("Данный пользователь уже существует");
        }
        setName(user);
        User createdUser = userStorage.addUser(user);
        log.info("User created: {}", createdUser);
        return createdUser;
    }

    public User update(User user) {
        UserValidator.validate(user);
        setName(user);
        User updatedUser = userStorage.updateUser(user).orElseThrow(() -> new NotFoundException(USER + user.getId()));
        log.info("User updated\nNew value: {}", updatedUser);
        return updatedUser;
    }


    public void remove(Integer id) {
        if (userStorage.removeById(id)) {
            log.info("User: {} - deleted", id);
        } else {
            throw new NotFoundException(USER + id);
        }
    }


    public void makeFriends(Integer userId, Integer friendId) {
        Integer[] numOfFriends = userStorage.addFriend(getById(userId), getById(friendId));
        log.info("User {}(total friends: {}) and {}(total friends: {}) - now friends",
                userId, numOfFriends[0], friendId, numOfFriends[1]);
    }

    public void destroyFriendship(Integer userId, Integer friendId) {
        if (!userStorage.removeFriend(getById(userId), getById(friendId))) {
            throw new NotFoundException("Друг " + userId + " или " + friendId);
        }
        log.info("Friendship between User#{} and User#{} is ruined... forever", userId, friendId);
    }

    public List<User> getFriends(Integer userId) {
        return userStorage.findFriends(getById(userId));
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        return userStorage.findCommonFriends(getById(userId), getById(friendId));
    }

    private void setName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
