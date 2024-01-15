package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.RecommendationStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RecommendationService {

    private FilmStorage filmStorage;
    private RecommendationStorage recommendationStorage;

    @Autowired
    public RecommendationService(FilmStorage filmStorage, RecommendationStorage recommendationDbStorage) {
        this.filmStorage = filmStorage;
        this.recommendationStorage = recommendationDbStorage;
    }

    public List<Film> getRecommendedFilms(Long userId) {
        List<Film> result = new ArrayList<>();
        for (Long recommendedFilm : recommendationStorage.recommendFilms(userId)) {
            result.add(filmStorage.getFilm(recommendedFilm));
        }

        return result;
    }


}
