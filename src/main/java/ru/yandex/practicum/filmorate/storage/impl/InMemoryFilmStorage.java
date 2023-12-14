package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoFilmFoundException;
import ru.yandex.practicum.filmorate.exception.NoUserLikesFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> filmsRate = new HashMap<>();
    private final LocalDate minDateRelease = LocalDate.parse("1895-12-28");
    private Long idFilmSequence = 1L;
    private final int countTopFilm = 10;

    @Override
    public List<Genre> getAllGenres() {
        throw new UnsupportedOperationException("Unsupported!");
    }

    @Override
    public List<Mpa> getAllMpa() {
        throw new UnsupportedOperationException("Unsupported!");
    }

    @Override
    public Film addLikeToFilm(Long filmId, Long userId) {
        log.debug("filmId {}, userId {}", filmId, userId);
        log.debug("Get list likes");
        Set<Long> likesList = Optional.ofNullable(filmsRate.get(filmId)).orElse(new HashSet<>());
        log.debug("Add like to list");
        likesList.add(userId);
        log.debug("Update film like-list");
        filmsRate.put(filmId, likesList);

        return getAllFilms().stream().filter(f -> f.getId() == filmId).findFirst().get();
    }

    @Override
    public List<Film> getTopNfilms(Integer count) {
        int countTop = count != null ? count : countTopFilm;
        log.debug("top films count = {}", countTop);

        return getAllFilms().stream()
                .sorted((o1, o2) -> {
                    Integer o1Size = 0;
                    Integer o2Size = 0;
                    Optional<Set<Long>> o1O = Optional.ofNullable(filmsRate.get(o1.getId()));
                    Optional<Set<Long>> o2O = Optional.ofNullable(filmsRate.get(o2.getId()));
                    if (o1O.isPresent()) {
                        o1Size = o1O.get().size();
                    }
                    if (o2O.isPresent()) {
                        o2Size = o2O.get().size();
                    }

                    return o2Size - o1Size;
                })
                .limit(countTop)
                .collect(Collectors.toList());
    }

    @Override
    public Film deleteLikeFromFilm(Long filmId, Long userId) {
        log.debug("filmId {}, userId {}", filmId, userId);
        Optional<Set<Long>> likesListO = Optional.ofNullable(filmsRate.get(filmId));
        if (!likesListO.isPresent()) {
            throw new NoUserLikesFoundException("Film " + filmId + " did not have any likes");
        } else {
            Optional<Long> likeExistO = likesListO.get().stream().filter(f -> f.longValue() == userId).findFirst();
            if (likeExistO.isPresent()) {
                likesListO.get().removeIf(uId -> uId.longValue() == userId);
                filmsRate.put(filmId, likesListO.get());
            } else {
                throw new NoUserLikesFoundException("Film " + filmId + " did not have Likes from user " + userId);
            }
        }

        return getAllFilms().stream().filter(f -> f.getId() == filmId).findFirst().get();
    }

    @Override
    public List<Film> getAllFilms() {
        return films.values().stream().collect(Collectors.toList());
    }

    @Override
    public Film addFilm(Film film) {
        log.debug(film.toString());
        validateFilm(film);
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
        validateFilm(film);
        log.debug("update film validation success");
        if (!films.keySet().stream().filter(k -> k.equals(film.getId())).findFirst().isPresent()) {
            log.error("updating film not exists!");
            throw new NoFilmFoundException("updating film not exists!");
        }
        films.put(film.getId(), film);
        log.debug("updating film in map");

        return film;
    }

    @Override
    public Film getFilm(Long filmId) {
        try {
            return getAllFilms().stream().filter(f -> f.getId() == filmId).findFirst().get();
        } catch (NoSuchElementException e) {
            throw new NoFilmFoundException(e.getMessage());
        }
    }

    private void validateFilm(Film film) {
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
