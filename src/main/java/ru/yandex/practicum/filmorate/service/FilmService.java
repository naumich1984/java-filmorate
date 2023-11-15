package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoFilmFoundException;
import ru.yandex.practicum.filmorate.exception.NoUserLikesFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final int countTopFilm = 10;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addLikeToFilm(Long filmId, Long userId) {
        log.debug("filmId {}, userId {}", filmId, userId);
        log.debug("Get list likes");
        Set<Long> likesList = Optional.ofNullable(filmStorage.getFilmsLikesStorage().get(filmId)).orElse(new HashSet<>());
        log.debug("Add like to list");
        likesList.add(userId);
        log.debug("Update film like-list");
        filmStorage.getFilmsLikesStorage().put(filmId, likesList);

        return filmStorage.allFilms().stream().filter(f -> f.getId() == filmId).findFirst().get();
    }

    public Film deleteLikeFromFilm(Long filmId, Long userId) {
        log.debug("filmId {}, userId {}", filmId, userId);
        Optional<Set<Long>> likesListO = Optional.ofNullable(filmStorage.getFilmsLikesStorage().get(filmId));
        if (!likesListO.isPresent()) {
            throw new NoUserLikesFoundException("Film " + filmId + " did not have any likes");
        } else {
            Optional<Long> likeExistO = likesListO.get().stream().filter(f -> f.longValue() == userId).findFirst();
            if (likeExistO.isPresent()) {
                likesListO.get().removeIf(uId -> uId.longValue() == userId);
                filmStorage.getFilmsLikesStorage().put(filmId, likesListO.get());
            } else {
                throw new NoUserLikesFoundException("Film " + filmId + " did not have Likes from user " + userId);
            }
        }

        return filmStorage.allFilms().stream().filter(f -> f.getId() == filmId).findFirst().get();
    }

    public List<Film> getTopNfilms(Integer count) {
        int countTop = Optional.ofNullable(count).orElse(countTopFilm);
        log.debug("top films count = {}", countTop);

        return filmStorage.allFilms().stream()
                .sorted((o1, o2) -> {
                            Integer o1Size = 0;
                            Integer o2Size = 0;
                            Optional<Set<Long>> o1O = Optional.ofNullable(filmStorage.getFilmsLikesStorage().get(o1.getId()));
                            Optional<Set<Long>> o2O = Optional.ofNullable(filmStorage.getFilmsLikesStorage().get(o2.getId()));
                            if (o1O.isPresent()) {o1Size = o1O.get().size();}
                            if (o2O.isPresent()) {o2Size = o2O.get().size();}

                            return o2Size - o1Size;
                        })
                .limit(countTop)
                .collect(Collectors.toList());
    }

    public Film getFilmById(Long filmId) {
        log.debug("filmId {}", filmId);
        try {
            return filmStorage.allFilms().stream().filter(f -> f.getId() == filmId).findFirst().get();
        } catch(NoSuchElementException e) {
            throw new NoFilmFoundException(e.getMessage());
        }
    }
}
