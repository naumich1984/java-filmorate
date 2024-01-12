package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoFilmFoundException;
import ru.yandex.practicum.filmorate.exception.NoGenreFoundException;
import ru.yandex.practicum.filmorate.exception.NoUserFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
@Data
public class FilmService {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    private final FeedStorage feedStorage;
    private final int countTopFilm = 10;

    @Autowired
    public FilmService(FilmStorage filmStorage, FeedStorage feedStorage) {
        this.filmStorage = filmStorage;
        this.feedStorage = feedStorage;
    }

    public Genre getGenre(Integer genreId) {
        log.debug("genreId {}", genreId);
        log.debug("Get Genre");
        try {
            return filmStorage.getAllGenres().stream()
                    .filter(f -> f.getId().equals(genreId)).findFirst().get();
        } catch (NoSuchElementException ex) {
            throw new NoGenreFoundException(ex.getMessage());
        }
    }

    public Mpa getMpa(Integer mpaId) {
        log.debug("mpaId {}", mpaId);
        log.debug("Get Mpa");
        try {
            return filmStorage.getAllMpa().stream()
                    .filter(f -> f.getId().equals(mpaId)).findFirst().get();
        } catch (NoSuchElementException ex) {
            throw new NoGenreFoundException(ex.getMessage());
        }
    }

    public Film addLikeToFilm(Long filmId, Long userId) {
        log.debug("filmId {}, userId {}", filmId, userId);
        log.debug("addLikeToFilm");
        Film filmResult = filmStorage.addLikeToFilm(filmId, userId);
        //Делаем запись в истории для ленты событий
        if (Optional.ofNullable(filmResult).isPresent()) {
            feedStorage.addFeedEntity(userId, FeedEventType.LIKE, FeedOperations.ADD, filmId);
        }

        return filmResult;
    }

    public Film deleteLikeFromFilm(Long filmId, Long userId) {
        log.debug("filmId {}, userId {}", filmId, userId);
        log.debug("deleteLikeFromFilm");
        Film filmResult = filmStorage.deleteLikeFromFilm(filmId, userId);
        //Делаем запись в истории для ленты событий
        if (Optional.ofNullable(filmResult).isPresent()) {
            feedStorage.addFeedEntity(userId, FeedEventType.LIKE, FeedOperations.REMOVE, filmId);
        }

        return filmResult;
    }

    public List<Film> getTopNfilms(Integer count, Integer genreId, Integer year) {
        int countTop = Optional.ofNullable(count).orElse(countTopFilm);
        log.debug("top films count = {}", countTop);

        return filmStorage.getTopNfilms(countTop, genreId, year);
    }

    public Film getFilm(Long filmId) {
        log.debug("filmId {}", filmId);
        try {
            Film film = filmStorage.getFilm(filmId);
            if (film == null) throw new NoUserFoundException("Film not found!");
            return film;
        } catch (NoSuchElementException e) {
            throw new NoUserFoundException(e.getMessage());
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
                throw new NoFilmFoundException("Something went wrong!");
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
