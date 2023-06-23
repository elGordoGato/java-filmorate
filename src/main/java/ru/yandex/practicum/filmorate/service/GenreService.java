package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.validation.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GenreService {
    private static final String GENRE = "Жанр";
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getById(Integer id) {
        Optional<Genre> genre = genreStorage.findById(id);
        if (genre.isEmpty()) {
            throw new NotFoundException(GENRE + id);
        }
        log.info("Genre found: {}", genre.get());
        return genre.get();
    }

    public List<Genre> getAll() {
        return genreStorage.findAll();
    }
}
