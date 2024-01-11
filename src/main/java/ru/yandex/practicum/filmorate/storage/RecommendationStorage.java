package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface RecommendationStorage {

    List<Long> recommendFilms(Long targetUserId);
}
