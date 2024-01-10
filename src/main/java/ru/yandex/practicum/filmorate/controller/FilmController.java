package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

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
        log.debug("POST /films request");

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

    @GetMapping("/directors")
    public ResponseEntity getAllDirectors() {
        log.debug("GET /directors request");

        return ResponseEntity.ok(filmService.getFilmStorage().getAllDirectors());
    }

    @GetMapping("/directors/{id}")
    public ResponseEntity getDirector(@PathVariable Integer id) {
        log.debug("GET /directors/{id} request");

        return ResponseEntity.ok(filmService.getDirector(id));
    }

    @PostMapping("/directors")
    public ResponseEntity createDirector(@RequestBody Director director) {
        log.debug("POST /directors request");

        return ResponseEntity.ok(filmService.createDirector(director));
    }

    @PutMapping("/directors")
    public ResponseEntity updateDirector(@RequestBody Director director) {
        log.debug("PUT /directors request");

        return ResponseEntity.ok(filmService.updateDirector(director));
    }

    @DeleteMapping("/directors/{id}")
    public ResponseEntity deleteDirector(@PathVariable Integer id) {
        log.debug("DELETE /directors/{id}");

        return ResponseEntity.ok(filmService.deleteDirector(id));
    }

    @GetMapping("/films/director/{directorId}")
    public ResponseEntity getDirectorsFilmSortBy(@PathVariable Integer directorId,
                                        @RequestParam(value = "sortBy", required = false) String sortBy) {
        log.debug("GET /films/director/{directorId}?sortBy");

        return ResponseEntity.ok(filmService.getDirectorsFilmSortBy(directorId, sortBy));
    }

    @GetMapping("films/search")
    public ResponseEntity getFilmByQuery(@RequestParam(value = "query", defaultValue="empty") String query,
                                         @RequestParam(value = "by", defaultValue="empty") String by) {
        log.debug("GET /films/search?query={query}&by={List.of(director,title)}");

        return ResponseEntity.ok(filmService.getFilmStorage().getFilmByQuery(query, by));
    }

}
