package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
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

    Integer deleteFilm(Long filmId);

    List<Director> getAllDirectors();

    Director getDirector(Integer id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    Integer deleteDirector(Integer id);

    List<Film> getDirectorsFilmSortBy(Integer directorId, String sort);

    List<Film> getMostPopular(Integer count, Integer genreId, Integer year);
}
