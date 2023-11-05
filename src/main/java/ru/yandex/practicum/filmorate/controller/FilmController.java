package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private final LocalDate MIN_DATE_RELEASE = LocalDate.parse("1895-12-28");
    private Integer idFilmSequence = 1;

    @GetMapping("/films")
    public ResponseEntity allFilms() {
        log.debug("GET /films request");

        return ResponseEntity.ok(films.values().stream().collect(Collectors.toList()));
    }

    @PostMapping("/films")
    public ResponseEntity addFilm(@RequestBody @Valid @NotNull Film film) {
        try {
            log.debug("POST /users request");
            log.debug(film.toString());
            validationFilm(film);
            log.debug("add film validation success");
            Film filmValidated = film;
            if (films.values().stream().filter(f ->  f.equals(filmValidated)).findFirst().isPresent()) {
                log.error("adding film already exists!");
                throw new ValidationException("adding film already exists!");
            }
            Integer newFilmId = idFilmSequence++;
            filmValidated.setId(newFilmId);
            log.debug("set film id {}", idFilmSequence - 1);
            films.put(newFilmId, filmValidated);
            log.debug("Put film into map");
            return ResponseEntity.ok(film);
        } catch (ValidationException e) {
            log.error("add film error:{}", e.getMessage());
            log.error("add film trace:{}", e.getStackTrace());

            return ResponseEntity.badRequest().body(film);
        }
    }

    @PutMapping("/films")
    public ResponseEntity updateFilm(@RequestBody @Valid @NotNull Film film) {
        try {
            log.debug("PUT /users request");
            log.debug(film.toString());
            validationFilm(film);
            log.debug("update film validation success");
            if (!films.keySet().stream().filter(k -> k.equals(film.getId())).findFirst().isPresent()) {
                log.error("updating film not exists!");
                throw new ValidationException("updating film not exists!");
            }
            films.put(film.getId(), film);
            log.debug("updating film in map");
            return ResponseEntity.ok(film);
        } catch (ValidationException e) {
            log.error("update film error:{}", e.getMessage());
            log.error("update film trace:{}", e.getStackTrace());

            return ResponseEntity.internalServerError().body(film);
        }
    }

    private void validationFilm(Film film) {
        log.debug("validation film");
        Optional<Film> filmO = Optional.ofNullable(film);
        if (filmO.isPresent()) {
            log.debug("check releaseDate");
            Optional<LocalDate> releaseDateO = Optional.ofNullable(filmO.get().getReleaseDate());
             if (releaseDateO.isPresent() && releaseDateO.get().isBefore(MIN_DATE_RELEASE)) {
                 log.error("adding film releaseDate is before " + MIN_DATE_RELEASE.toString() + "!");
                  throw new ValidationException("adding film releaseDate is before " + MIN_DATE_RELEASE.toString() + "!");
            }
        } else {
            throw new ValidationException("adding film is null!");
        }
    }
}
