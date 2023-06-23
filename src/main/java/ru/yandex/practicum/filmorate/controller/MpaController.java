package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController {

    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public List<Mpa> getAll() {
        log.info("Request to get all mpa");
        List<Mpa> allMpa = mpaService.getAll();
        log.info("Found {} mpa: {}", allMpa.size(), allMpa.stream()
                .map(mpa -> String.format("Mpa #%s - %s\n", mpa.getId(), mpa.getName()))
                .collect(Collectors.toList()));
        return allMpa;
    }

    @GetMapping(value = "/{id}")
    public Mpa getById(@PathVariable Integer id) {
        log.info("Request to get mpa with id: {}", id);
        return mpaService.getById(id);
    }
}
