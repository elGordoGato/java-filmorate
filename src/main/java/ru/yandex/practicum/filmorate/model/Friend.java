package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.Set;

@Data
public class Friend {
    private Set<Integer> friends;
}
