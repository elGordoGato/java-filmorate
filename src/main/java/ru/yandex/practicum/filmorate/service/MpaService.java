package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.validation.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Slf4j
@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Mpa getById(Integer id) {
        Mpa mpa = mpaStorage.findById(id).orElseThrow(() -> new NotFoundException("Возрастной рейтинг " + id));
        log.info("Mpa found: {}", mpa);
        return mpa;
    }

    public List<Mpa> getAll() {
        return mpaStorage.findAll();
    }
}
