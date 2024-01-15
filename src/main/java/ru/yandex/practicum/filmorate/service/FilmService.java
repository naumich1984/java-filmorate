package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final FeedStorage feedStorage;

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public List<Mpa> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getCommonFilms(long userId, Long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> getFilmByQuery(String query, String by) {
        return filmStorage.getFilmByQuery(query, by);
    }

    public Genre getGenre(Integer genreId) {
        log.debug("genreId {}", genreId);
        log.debug("Get Genre");
        try {
            return filmStorage.getAllGenres().stream()
                    .filter(f -> f.getId().equals(genreId)).findFirst().get();
        } catch (NoSuchElementException ex) {
            throw new NotFoundException(ex.getMessage());
        }
    }

    public Mpa getMpa(Integer mpaId) {
        log.debug("mpaId {}", mpaId);
        log.debug("Get Mpa");
        try {
            return filmStorage.getAllMpa().stream()
                    .filter(f -> f.getId().equals(mpaId)).findFirst().get();
        } catch (NoSuchElementException ex) {
            throw new NotFoundException(ex.getMessage());
        }
    }

    public Film addLikeToFilm(Long filmId, Long userId) {
        log.debug("filmId {}, userId {}", filmId, userId);
        log.debug("addLikeToFilm");
        Film filmResult = filmStorage.addLikeToFilm(filmId, userId);
        //Делаем запись в истории для ленты событий
        if (filmResult != null) {
            feedStorage.addFeedEntity(userId, FeedEventType.LIKE, FeedOperations.ADD, filmId);
        }

        return filmResult;
    }

    public Film deleteLikeFromFilm(Long filmId, Long userId) {
        log.debug("filmId {}, userId {}", filmId, userId);
        log.debug("deleteLikeFromFilm");
        Film filmResult = filmStorage.deleteLikeFromFilm(filmId, userId);
        //Делаем запись в истории для ленты событий
        if (filmResult != null) {
            feedStorage.addFeedEntity(userId, FeedEventType.LIKE, FeedOperations.REMOVE, filmId);
        }

        return filmResult;
    }

    public List<Film> getTopNfilms(Integer count, Integer genreId, Integer year) {
        log.debug("top films count = {}", count);

        return filmStorage.getTopNfilms(count, genreId, year);
    }

    public Film getFilm(Long filmId) {
        log.debug("filmId {}", filmId);
        try {
            Film film = filmStorage.getFilm(filmId);
            if (film == null) throw new NotFoundException("Film not found!");
            return film;
        } catch (NoSuchElementException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    public String deleteFilm(Long filmId) {
        int result = filmStorage.deleteFilm(filmId);
        switch (result) {
            case 0: {
                log.debug("There is no film with id={}", filmId);
                return "Film with id = " + filmId + " is not exist.";
            }
            case 1: {
                log.debug("Film with id={} has been deleted successfully.", filmId);
                return "Film with id = " + filmId + " has been deleted successfully\".";
            }
            default: {
                log.error("DELETED MORE THAN ONE FILM");
                throw new NotFoundException("Something went wrong!");
            }
        }
    }

    public List<Director> getAllDirectors() {
        log.debug("getAllDirectors");

        return filmStorage.getAllDirectors();
    }

    public Director getDirector(Integer id) {
        log.debug("getDirector");

        return filmStorage.getDirector(id);
    }

    public Director createDirector(Director director) {
        log.debug("createDirector");

        return filmStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        log.debug("updateDirector");

        return filmStorage.updateDirector(director);
    }

    public Integer deleteDirector(Integer id) {
        log.debug("deleteDirector");

        return filmStorage.deleteDirector(id);
    }

    public List<Film> getDirectorsFilmSortBy(Integer directorId, String sort) {
        log.debug("getDirectorsFilmLikeOrder");

        return filmStorage.getDirectorsFilmSortBy(directorId, sort);
    }
}
