package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoFilmFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> filmsRate = new HashMap<>();
    private final LocalDate minDateRelease = LocalDate.parse("1895-12-28");
    private Long idFilmSequence = 1L;

    @Override
    public Map<Long, Set<Long>> getFilmsLikesStorage() {
        return filmsRate;
    }

    @Override
    public List<Film> allFilms() {
        return films.values().stream().collect(Collectors.toList());
    }

    @Override
    public Film addFilm(Film film) {
        log.debug(film.toString());
        validationFilm(film);
        log.debug("add film validation success");
        Film filmValidated = film;
        if (films.values().stream().filter(f -> f.equals(filmValidated)).findFirst().isPresent()) {
            log.error("adding film already exists!");
            throw new ValidationException("adding film already exists!");
        }
        Long newFilmId = idFilmSequence++;
        filmValidated.setId(newFilmId);
        log.debug("set film id {}", newFilmId);
        films.put(newFilmId, filmValidated);
        log.debug("Put film into map");

        return filmValidated;
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug(film.toString());
        validationFilm(film);
        log.debug("update film validation success");
        if (!films.keySet().stream().filter(k -> k.equals(film.getId())).findFirst().isPresent()) {
            log.error("updating film not exists!");
            throw new NoFilmFoundException("updating film not exists!");
        }
        films.put(film.getId(), film);
        log.debug("updating film in map");

        return film;
    }

    private void validationFilm(Film film) {
        log.debug("validation film");
        try {
            if (List.of(film).stream().filter(f -> f.getReleaseDate().isBefore(minDateRelease)).findFirst().isPresent()) {
                log.error("adding film releaseDate is before " + minDateRelease.toString() + "!");
                throw new ValidationException("adding film releaseDate is before " + minDateRelease.toString() + "!");
            }
        } catch (NullPointerException e) {
            throw new ValidationException("adding film is null!");
        }
    }
}
