package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class User {
    @NonNull
    private final String email;
    @NonNull
    private final String login;
    @NonNull
    private final LocalDate birthday;
    private Integer id;
    private String name;
    private Set<Integer> friends;
}
