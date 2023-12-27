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

@RestController
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }


    @GetMapping("/genres")
    public ResponseEntity getAllGenres() {
        log.debug("GET /genres request");

        return ResponseEntity.ok(filmService.getFilmStorage().getAllGenres());
    }


    @GetMapping("/genres/{id}")
    public ResponseEntity getGenre(@PathVariable int id) {
        log.debug("GET /genres/{id} request");

        return ResponseEntity.ok(filmService.getGenre(id));
    }

    @GetMapping("/mpa")
    public ResponseEntity getAllMpa() {
        log.debug("GET /mpa request");

        return ResponseEntity.ok(filmService.getFilmStorage().getAllMpa());
    }


    @GetMapping("/mpa/{id}")
    public ResponseEntity getMpa(@PathVariable Integer id) {
        log.debug("GET /mpa/{id} request");

        return ResponseEntity.ok(filmService.getMpa(id));
    }


    @GetMapping("/films")
    public ResponseEntity getAllFilms() {
        log.debug("GET /films request");

        return ResponseEntity.ok(filmService.getFilmStorage().getAllFilms());
    }

    @PostMapping("/films")
    public ResponseEntity addFilm(@RequestBody @Valid @NotNull Film film) {
        log.debug("POST /users request");

        return ResponseEntity.ok(filmService.getFilmStorage().addFilm(film));
    }

    @PutMapping("/films")
    public ResponseEntity updateFilm(@RequestBody @Valid @NotNull Film film) {
        log.debug("PUT /films request");

        return ResponseEntity.ok(filmService.getFilmStorage().updateFilm(film));
    }

    @PutMapping("/films/{id}/like/{userId}")
    public ResponseEntity addLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("PUT /films/{id}/like/{userId} request");

        return ResponseEntity.ok(filmService.addLikeToFilm(id, userId));
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public ResponseEntity deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("DELETE /films/{id}/like/{userId} request");

        return ResponseEntity.ok(filmService.deleteLikeFromFilm(id, userId));
    }

    @GetMapping("/films/popular")
    public ResponseEntity getTopFilms(@RequestParam(required = false) Integer count) {
        log.debug("GET /films/popular?count={count} request");

        return ResponseEntity.ok(filmService.getTopNfilms(count));
    }

    @GetMapping("/films/{id}")
    public ResponseEntity getFilm(@PathVariable long id) {
        log.debug("GET /films/{id} request");

        return ResponseEntity.ok(filmService.getFilm(id));
    }

    @DeleteMapping("/films/{filmId}")
    public ResponseEntity deleteFilm(@PathVariable Long filmId) {
        log.debug("DELETE /films/{id} request");

        return ResponseEntity.ok(filmService.deleteFilm(filmId));
    }

}
