package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class User {
    public static int counter = 1;
    @NonNull
    private final String email;
    @NonNull
    private final String login;
    @NonNull
    private final LocalDate birthday;
    private Integer id;
    private String name;

}
