package ru.yandex.practicum.filmorate.storage;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.impl.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.RecommendationDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RecommendationDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    UserService userService;
    FilmService filmService;
    RecommendationStorage recommendationStorage;


    @BeforeEach
    public void setup() {
        userService = new UserService(new UserDbStorage(jdbcTemplate), new FeedDbStorage(jdbcTemplate));
        filmService = new FilmService(new FilmDbStorage(jdbcTemplate), new FeedDbStorage(jdbcTemplate));
        recommendationStorage = new RecommendationDbStorage(jdbcTemplate);

        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1").name("USER_NAME")
                .birthday(LocalDate.of(1998, 8, 16))
                .build();
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("user1").name("USER_2_NAME")
                .birthday(LocalDate.of(1998, 8, 16))
                .build();
        User user3 = User.builder()
                .email("user3@yandex.ru")
                .login("user3")
                .name("USER3_NAME")
                .birthday(LocalDate.of(1998, 8, 16))
                .build();

        userService.addUser(user1);
        userService.addUser(user2);
        userService.addUser(user3);

        Film film1 = Film.builder()
                .name("Форсаж")
                .mpa(new Mpa(1, null))
                .description("гонки 1")
                .duration(90)
                .releaseDate(LocalDate.of(2001, 1, 1))
                .genres(new ArrayList<>())
                .directors(new ArrayList<>())
                .build();
        Film film2 = Film.builder()
                .name("Форсаж 2")
                .mpa(new Mpa(1, null))
                .description("гонки 2")
                .duration(90)
                .releaseDate(LocalDate.of(2005, 5, 5))
                .genres(new ArrayList<>())
                .directors(new ArrayList<>())
                .build();
        Film film3 = Film.builder()
                .name("Форсаж 3")
                .mpa(new Mpa(1, null))
                .description("гонки 3")
                .duration(90)
                .releaseDate(LocalDate.of(2008, 8, 8))
                .genres(new ArrayList<>())
                .directors(new ArrayList<>())
                .build();
        filmService.addFilm(film1);
        filmService.addFilm(film2);
        filmService.addFilm(film3);

    }

    @Test
    public void withNoLikes() {

        List<Long> shouldBeEmpty = recommendationStorage.recommendFilms(1L);
        assertThat(shouldBeEmpty.size()).isEqualTo(0);
    }

    @Test
    public void withOnlyFirstUserLikes() {
        filmService.addLikeToFilm(1L, 1L);
        List<Long> shouldBeEmpty = recommendationStorage.recommendFilms(1L);
        assertThat(shouldBeEmpty.size()).isEqualTo(0);
    }

    @Test
    public void withSameFirstUserLikes() {
        filmService.addLikeToFilm(1L, 1L);
        filmService.addLikeToFilm(1L, 2L);
        List<Long> shouldBeEmpty = recommendationStorage.recommendFilms(1L);
        assertThat(shouldBeEmpty.size()).isEqualTo(0);
    }

    @Test
    public void withNoSameFirstUserLikes() {
        filmService.addLikeToFilm(1L, 1L);
        filmService.addLikeToFilm(2L, 2L);
        List<Long> shouldBeEmpty = recommendationStorage.recommendFilms(1L);
        assertThat(shouldBeEmpty.size()).isEqualTo(1);
        assertThat(shouldBeEmpty.get(0)).isEqualTo(2L);
    }

    @Test
    public void withSameLikesWithThirdUser() {
        filmService.addLikeToFilm(1L, 1L);
        filmService.addLikeToFilm(2L, 1L);
        filmService.addLikeToFilm(1L, 3L);
        filmService.addLikeToFilm(2L, 3L);
        filmService.addLikeToFilm(3L, 3L);
        List<Long> shouldBeEmpty = recommendationStorage.recommendFilms(1L);
        assertThat(shouldBeEmpty.size()).isEqualTo(1);
        assertThat(shouldBeEmpty.get(0)).isEqualTo(3L);
    }


}
