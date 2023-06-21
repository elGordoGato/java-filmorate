package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository("UserDbStorage")
public class UserDbStorage implements UserStorage{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }


    @Override
    public Optional<User> putUser(User user) {
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
        if(userRows.next()) {
            user = User.builder()
                    .id(userRows.getInt("id"))
                    .email(Objects.requireNonNull(userRows.getString("email")))
                    .login(Objects.requireNonNull(userRows.getString("login")))
                    .birthday(Objects.requireNonNull(userRows.getDate("birthday")))
                    .name(userRows.getString("name"))
                    .build();
        }
        return Optional.ofNullable(user);
    }

    @Override
    public boolean removeById(Integer id) {
            String sqlQuery = "delete from film_user where id = ?";
            return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public Set<User> findAll() {
        String sql = "select * from film_user";
        return
        jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        // используем конструктор, методы ResultSet
        // и готовое значение user
        Integer id = rs.getInt("id");
        String description = rs.getString("description");
        String photoUrl = rs.getString("photo_url");

        // Получаем дату и конвертируем её из sql.Date в time.LocalDate
        LocalDate creationDate = rs.getDate("creation_date").toLocalDate();

        return new Post(id, user, description, photoUrl, creationDate);
    }
    }

    @Override
    public Integer[] addFriend(User user, User friend) {
        return new Integer[0];
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
    public Set<User> findCommonFriends(User user, User friend) {
        return null;
    }
}
