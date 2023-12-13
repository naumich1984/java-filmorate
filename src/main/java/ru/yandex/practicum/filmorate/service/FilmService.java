package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoGenreFoundException;
import ru.yandex.practicum.filmorate.exception.NoUserFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class FilmService {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    private final int countTopFilm = 10;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Genre getGenre(Integer genreId) {
        log.debug("genreId {}", genreId);
        log.debug("Get Genre");
        try {
            return filmStorage.allGenres().stream()
                    .filter(f -> f.getId().equals(genreId)).findFirst().get();
        } catch (NoSuchElementException ex) {
            throw new NoGenreFoundException(ex.getMessage());
        }
    }

    public Mpa getMpa(Integer mpaId) {
        log.debug("mpaId {}", mpaId);
        log.debug("Get Mpa");
        try {
            return filmStorage.allMpa().stream()
                    .filter(f -> f.getId().equals(mpaId)).findFirst().get();
        } catch (NoSuchElementException ex) {
            throw new NoGenreFoundException(ex.getMessage());
        }
    }

    public Film addLikeToFilm(Long filmId, Long userId) {
        log.debug("filmId {}, userId {}", filmId, userId);
        log.debug("Get list likes");

        return filmStorage.addLikeToFilm(filmId, userId);
    }

    public Film deleteLikeFromFilm(Long filmId, Long userId) {
        log.debug("filmId {}, userId {}", filmId, userId);

        return filmStorage.deleteLikeFromFilm(filmId, userId);
    }

    public List<Film> getTopNfilms(Integer count) {
        int countTop = Optional.ofNullable(count).orElse(countTopFilm);
        log.debug("top films count = {}", countTop);

        return filmStorage.getTopNfilms(countTop);
    }

    public Film getFilm(Long filmId) {
        log.debug("filmId {}", filmId);
        try {
            Film film = filmStorage.getFilm(filmId);
            if (film == null)  throw new NoUserFoundException("Film not found!");
            return film;
        } catch (NoSuchElementException e) {
            throw new NoUserFoundException(e.getMessage());
        }
    }
}
