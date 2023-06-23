package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Optional<User> addUser(User user) {
        String sqlQuery = "insert into film_user(id, email, login, birthday, name) " +
                "values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getName());
        return findById(user.getId());
    }

    @Override
    public Optional<User> findById(Integer id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from film_user where id = ?", id);
        User user = null;
        if (userRows.next()) {
            user = User.builder()
                    .id(userRows.getInt("id"))
                    .email(Objects.requireNonNull(userRows.getString("email")))
                    .login(Objects.requireNonNull(userRows.getString("login")))
                    .birthday(Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate())
                    .name(userRows.getString("name"))
                    .build();
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> updateUser(User user) {
        String sqlQuery = "update film_user set " +
                "email = ?, login = ?, birthday = ?, name = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getName(),
                user.getId());
        return findById(user.getId());
    }

    @Override
    public boolean removeById(Integer id) {
        String sqlQuery = "delete from film_user where id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public List<User> findAll() {
        String sql = "select * from film_user";
        return
                jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt("id"))
                .email(Objects.requireNonNull(rs.getString("email")))
                .login(Objects.requireNonNull(rs.getString("login")))
                .birthday(Objects.requireNonNull(rs.getDate("birthday")).toLocalDate())
                .name(rs.getString("name"))
                .build();
    }

    @Override
    public Integer[] addFriend(User user, User friend) {
        String sqlQuery = "insert into user_friends (user_id, friend_id) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery,
                user.getId(),
                friend.getId());
        return new Integer[]{findFriends(user).size(), findFriends(friend).size()};
    }

    @Override
    public boolean removeFriend(User user, User friend) {
        String sqlQuery = "delete from user_friends where user_id = ? AND friend_id = ?";
        return jdbcTemplate.update(sqlQuery, user.getId(), friend.getId()) > 0;
    }

    @Override
    public List<User> findFriends(User user) {
        String sql = "select * from film_user WHERE id IN (SELECT friend_id " +
                "FROM user_friends " +
                "WHERE user_id = ? " +
                "ORDER BY friend_id)";
        return
                jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), user.getId());
    }

    @Override
    public List<User> findCommonFriends(User user, User friend) {
        String sql = "SELECT * FROM film_user " +
                "WHERE id IN (SELECT friend_id " +
                "FROM user_friends " +
                "WHERE user_id = ? " +
                "AND friend_id IN (SELECT friend_id " +
                "FROM user_friends " +
                "WHERE user_id = ?))" +
                "ORDER BY id;";
        return
                jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), user.getId(), friend.getId());
    }
}
