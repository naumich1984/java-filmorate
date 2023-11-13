package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final int countTopFilm = 10;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addLikeToFilm(Long filmId, Long UserId) {
        Set<Long> likesList = Optional.ofNullable(filmStorage.getFilmsLikesStorage().get(filmId)).orElse(new HashSet<>());
        likesList.add(UserId);
        filmStorage.getFilmsLikesStorage().put(filmId, likesList);

        return filmStorage.allFilms().stream().filter(f -> f.getId() == filmId).findFirst().get();
    }

    public Film deleteLikeFromFilm(Long filmId, Long userId) {
        Set<Long> likesList = Optional.ofNullable(filmStorage.getFilmsLikesStorage().get(filmId)).orElse(new HashSet<>());
        likesList.removeIf(uId -> uId.longValue() == userId);
        filmStorage.getFilmsLikesStorage().put(filmId, likesList);

        return filmStorage.allFilms().stream().filter(f -> f.getId() == filmId).findFirst().get();
    }

    public List<Film> getTopNfilms(Integer count) {
        int countTop = Optional.ofNullable(count).orElse(countTopFilm);
        return filmStorage.allFilms().stream()
                .sorted(Comparator.comparingInt(f -> filmStorage.getFilmsLikesStorage().get(f.getId()).size()))
                .limit(countTop)
                .collect(Collectors.toList());
    }
}
