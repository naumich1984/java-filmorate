package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.RecommendationStorage;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
@Slf4j
public class RecommendationDbStorage implements RecommendationStorage {


    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RecommendationDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Long> recommendFilms(Long targetUserId) {

        // Создаём мапу где ключ-id пользователя, а значение лист с лайкнутыми фильмами
        Map<Long, List<Long>> userAndFilms = usersAndLikedFilms();

        if (!userAndFilms.containsKey(targetUserId)) {
            log.info("Target user didn't put a like");
            return List.of();
        }

        // Создаём мапу где ключ-id пользователя, а значение количество пересечений
        Map<Long, Integer> intersectionCounts = new HashMap<>();

        // Находим количество пересечений лайков для каждой пары пользователей
        for (Long userId : userAndFilms.keySet()) {
            if (!userId.equals(targetUserId)) {
                Set<Long> intersection = new HashSet<>(userAndFilms.get(userId));
                intersection.retainAll(userAndFilms.get(targetUserId));
                intersectionCounts.put(userId, intersection.size());
            }
        }


        // Создаём список пользователей
        List<Long> sortedUsers = new ArrayList<>(userAndFilms.keySet());
        // Удаляем нашего таргет пользователя, что бы не получить null
        sortedUsers.remove(targetUserId);
        // Сортируем пользователей по убыванию количества пересечений
        sortedUsers.sort(Comparator.comparingInt(intersectionCounts::get).reversed());

        // Находим первого по кол-ву совпадений пользователя
        Long mostSimilarUser = sortedUsers.stream().findFirst().orElse(-1L);

        List<Long> result = new ArrayList<>();
        //Добавляем фильмы наиболее похожего пользователя
        result.addAll(Optional.ofNullable(userAndFilms.get(mostSimilarUser)).orElse(new ArrayList<>()));
        //удаляем фильмы таргет пользователя
        result.removeAll(userAndFilms.get(targetUserId));

        return result;
    }


    public Map<Long, List<Long>> usersAndLikedFilms() {

        String sql = "SELECT * FROM FILMS_LIKES";
        List<Pair<Long, Long>> pairsUsersAndFilms = jdbcTemplate.query(sql, (rs, rowNum) -> mapUserIdAndFilmId(rs));

        Map<Long, List<Long>> usersAndLikedFilms = new HashMap<>();

        for (Pair<Long, Long> pairsUsersAndFilm : pairsUsersAndFilms) {
            Long userId = pairsUsersAndFilm.getFirst();
            Long filmId = pairsUsersAndFilm.getSecond();

            usersAndLikedFilms.computeIfAbsent(userId, k -> new ArrayList<>());

            usersAndLikedFilms.get(userId).add(filmId);

        }
        return usersAndLikedFilms;

    }


    Pair<Long, Long> mapUserIdAndFilmId(ResultSet resultSet) throws SQLException {
        Long userId = resultSet.getLong("USER_ID");
        Long filmId = resultSet.getLong("FILM_ID");

        Pair pair = Pair.of(userId, filmId);
        return pair;
    }

}
