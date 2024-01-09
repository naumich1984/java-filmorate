package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.impl.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FeedDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private Film newFilm;
    private User newUser;
    private Review newReview;
    private FilmDbStorage filmStorage;
    private UserDbStorage userStorage;
    private ReviewStorage reviewStorage;
    private FeedStorage feedStorage;

    @BeforeEach
    public void setup() {
        newFilm = new Film(1L, "film", "description", LocalDate.of(1991, 1, 1),
                120, Collections.emptyList(), Collections.emptyList(), new Mpa(1,"G"));
        newUser = new User(1L, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        newReview = new Review(1L,"review content", true, 1L, 1L, 0L);
        filmStorage = new FilmDbStorage(jdbcTemplate);
        userStorage = new UserDbStorage(jdbcTemplate);
        reviewStorage = new ReviewDbStorage(jdbcTemplate);
        feedStorage = new FeedDbStorage(jdbcTemplate);
        userStorage.addUser(newUser);
        filmStorage.addFilm(newFilm);
        reviewStorage.addReview(newReview);
    }

    @Test
    public void testAddFeed() {
        filmStorage.addLikeToFilm(newFilm.getId(), newUser.getId());

        Integer feed = feedStorage.addFeedEntity(newUser.getId(), FeedEventType.LIKE, FeedOperations.ADD, newFilm.getId());

        Assertions.assertEquals(feed, 0);
    }

    @Test
    public void testGetFeed() {
        feedStorage.addFeedEntity(newUser.getId(), FeedEventType.REVIEW, FeedOperations.ADD, newReview.getReviewId());

        List<Feed> feeds = feedStorage.getUserFeed(newUser.getId());

        Assertions.assertEquals(feeds.get(0).getEventType(), "REVIEW");
        Assertions.assertEquals(feeds.get(0).getOperation(), "ADD");
    }
}
