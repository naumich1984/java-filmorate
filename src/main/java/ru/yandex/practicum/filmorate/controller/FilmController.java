package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.websocket.server.PathParam;

@RestController
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public ResponseEntity allFilms() {
        log.debug("GET /films request");

        return ResponseEntity.ok(filmStorage.allFilms());
    }

    @PostMapping("/films")
    public ResponseEntity addFilm(@RequestBody @Valid @NotNull Film film) {
        log.debug("POST /users request");

        return ResponseEntity.ok(filmStorage.addFilm(film));
    }

    @PutMapping("/films")
    public ResponseEntity updateFilm(@RequestBody @Valid @NotNull Film film) {
        log.debug("PUT /users request");

        return ResponseEntity.ok(filmStorage.updateFilm(film));
    }

    @PutMapping("/films/{id}/like/{userId}")
    public ResponseEntity addLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("PUT /films/{id}/like/{userId} request");

        return ResponseEntity.ok(filmService.addLikeToFilm(id, userId));
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public ResponseEntity deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("PUT /films/{id}/like/{userId} request");

        return ResponseEntity.ok(filmService.deleteLikeFromFilm(id, userId));
    }

    @GetMapping("/films/popular")
    public ResponseEntity topFilms(@RequestParam int count) {
        log.debug("GET /films/popular?count={count} request");

        return ResponseEntity.ok(filmService.getTopNfilms(count));
    }

}
