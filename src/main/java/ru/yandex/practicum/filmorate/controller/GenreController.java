package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> getAll() {
        log.info("Request to get all genres");
        List<Genre> allGenres = genreService.getAll();
        log.info("Found {} genres: {}", allGenres.size(), allGenres.stream()
                .map(genre -> String.format("Genre #%s - %s\n", genre.getId(), genre.getName()))
                .collect(Collectors.toList()));
        return allGenres;
    }

    @GetMapping(value = "/{id}")
    public Genre getById(@PathVariable Integer id) {
        log.info("Request to get genre with id: {}", id);
        return genreService.getById(id);
    }
}
