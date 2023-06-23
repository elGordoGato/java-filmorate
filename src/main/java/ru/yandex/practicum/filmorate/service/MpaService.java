package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.validation.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MpaService {
    private static final String MPA = "Возрастной рейтинг";
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Mpa getById(Integer id) {
        Optional<Mpa> mpa = mpaStorage.findById(id);
        if (mpa.isEmpty()) {
            throw new NotFoundException(MPA + id);
        }
        log.info("Mpa found: {}", mpa.get());
        return mpa.get();
    }

    public List<Mpa> getAll() {
        return mpaStorage.findAll();
    }
}
