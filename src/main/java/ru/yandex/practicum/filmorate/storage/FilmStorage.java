package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmStorage {

    List<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(Long filmId);

    List<Genre> getAllGenres();

    List<Mpa> getAllMpa();

    Film addLikeToFilm(Long filmId, Long userId);

    List<Film> getTopNfilms(Integer count);

    Film deleteLikeFromFilm(Long filmId, Long userId);
}
