package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class User {
    private Integer id;
    @NonNull
    private final String email;
    @NonNull
    private final String login;
    @NonNull
    private final Date birthday;
    private String name;
    private Set<Integer> friends;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getEmail().equals(user.getEmail()) || getLogin().equals(user.getLogin()) || Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getLogin(), getId());
    }
}
