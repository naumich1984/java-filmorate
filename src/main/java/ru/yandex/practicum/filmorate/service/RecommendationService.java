package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.RecommendationStorage;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class RecommendationService {

    private FilmStorage filmStorage;
    private RecommendationStorage recommendationStorage;

    public List<Film> getRecommendedFilms(Long userId) {

        return filmStorage.getFilmsByIds(recommendationStorage.recommendFilms(userId));
    }
}
