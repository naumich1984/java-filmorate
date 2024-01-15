package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Validated
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @GetMapping("/genres")
    public ResponseEntity<List<Genre>> getAllGenres() {
        log.debug("GET /genres request");

        return ResponseEntity.ok(filmService.getFilmStorage().getAllGenres());
    }


    @GetMapping("/genres/{id}")
    public ResponseEntity<Genre> getGenre(@PathVariable int id) {
        log.debug("GET /genres/{id} request");
        log.debug("id: {}", id);

        return ResponseEntity.ok(filmService.getGenre(id));
    }

    @GetMapping("/mpa")
    public ResponseEntity<List<Mpa>> getAllMpa() {
        log.debug("GET /mpa request");

        return ResponseEntity.ok(filmService.getFilmStorage().getAllMpa());
    }


    @GetMapping("/mpa/{id}")
    public ResponseEntity<Mpa> getMpa(@PathVariable Integer id) {
        log.debug("GET /mpa/{id} request");
        log.debug("id: {}", id);

        return ResponseEntity.ok(filmService.getMpa(id));
    }


    @GetMapping("/films")
    public ResponseEntity<List<Film>> getAllFilms() {
        log.debug("GET /films request");

        return ResponseEntity.ok(filmService.getFilmStorage().getAllFilms());
    }

    @PostMapping("/films")
    public ResponseEntity<Film> addFilm(@RequestBody @Valid @NotNull Film film) {
        log.debug("POST /films request");

        return ResponseEntity.ok(filmService.getFilmStorage().addFilm(film));
    }

    @PutMapping("/films")
    public ResponseEntity<Film> updateFilm(@RequestBody @Valid @NotNull Film film) {
        log.debug("PUT /films request");

        return ResponseEntity.ok(filmService.getFilmStorage().updateFilm(film));
    }

    @PutMapping("/films/{id}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("PUT /films/{id}/like/{userId} request");
        log.debug("id: {}, userId: {}", id, userId);

        return ResponseEntity.ok(filmService.addLikeToFilm(id, userId));
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public ResponseEntity<Film> deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("DELETE /films/{id}/like/{userId} request");
        log.debug("id: {}, userId: {}", id, userId);

        return ResponseEntity.ok(filmService.deleteLikeFromFilm(id, userId));
    }

    @GetMapping("/films/popular")
    public ResponseEntity<List<Film>> getTopFilms(@RequestParam(defaultValue = "10") @Positive Integer count,
                                      @RequestParam(required = false) Integer genreId,
                                      @RequestParam(required = false) @Positive  @Min(1895) Integer year) {
        log.debug("GET /films/popular?count={limit}&genreId={genreId}&year={year}");
        log.debug("count: {}, genreId: {}, year: {}", count, genreId, year);

        return ResponseEntity.ok(filmService.getTopNfilms(count, genreId, year));
    }


    @GetMapping("/films/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable long id) {
        log.debug("GET /films/{id} request");
        log.debug("id: {}", id);

        return ResponseEntity.ok(filmService.getFilm(id));
    }

    @DeleteMapping("/films/{filmId}")
    public ResponseEntity<String> deleteFilm(@PathVariable Long filmId) {
        log.debug("DELETE /films/{id} request");
        log.debug("filmId: {}", filmId);

        return ResponseEntity.ok(filmService.deleteFilm(filmId));
    }

    @GetMapping("/directors")
    public ResponseEntity<List<Director>> getAllDirectors() {
        log.debug("GET /directors request");

        return ResponseEntity.ok(filmService.getFilmStorage().getAllDirectors());
    }

    @GetMapping("/directors/{id}")
    public ResponseEntity<Director> getDirector(@PathVariable Integer id) {
        log.debug("GET /directors/{id} request");
        log.debug("id: {}", id);

        return ResponseEntity.ok(filmService.getDirector(id));
    }

    @PostMapping("/directors")
    public ResponseEntity<Director> createDirector(@RequestBody Director director) {
        log.debug("POST /directors request");

        return ResponseEntity.ok(filmService.createDirector(director));
    }

    @PutMapping("/directors")
    public ResponseEntity<Director> updateDirector(@RequestBody Director director) {
        log.debug("PUT /directors request");

        return ResponseEntity.ok(filmService.updateDirector(director));
    }

    @DeleteMapping("/directors/{id}")
    public ResponseEntity<Integer> deleteDirector(@PathVariable Integer id) {
        log.debug("DELETE /directors/{id}");
        log.debug("id: {}", id);

        return ResponseEntity.ok(filmService.deleteDirector(id));
    }

    @GetMapping("/films/director/{directorId}")
    public ResponseEntity<List<Film>> getDirectorsFilmSortBy(@PathVariable Integer directorId,
                                        @RequestParam(value = "sortBy", required = false) DirectorSorting sortBy) {
        log.debug("GET /films/director/{directorId}?sortBy");
        log.debug("directorId: {}", directorId);

        return ResponseEntity.ok(filmService.getDirectorsFilmSortBy(directorId, sortBy.name()));
    }

    @GetMapping("films/search")
    public ResponseEntity<List<Film>> getFilmByQuery(@RequestParam(value = "query", defaultValue = "empty") String query,
                                         @RequestParam(value = "by", defaultValue = "empty") String by) {
        log.debug("GET /films/search?query={query}&by={List.of(director,title)}");
        log.debug("query: {}", query);

        return ResponseEntity.ok(filmService.getFilmStorage().getFilmByQuery(query, by));
    }

    @GetMapping("/films/common")
    public ResponseEntity<List<Film>> getCommonFilms(@RequestParam(required = true) long userId,
                                         @RequestParam(required = true) long friendId) {
        log.debug("GET /films/common?userId={userId}&friendId={friendId}");
        log.debug("userId: {}, friendId: {}", userId, friendId);

        return ResponseEntity.ok(filmService.getFilmStorage().getCommonFilms(userId, friendId));
    }

}
