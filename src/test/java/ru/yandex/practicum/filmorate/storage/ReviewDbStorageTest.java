package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReviewDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private Film newFilm;
    private User newUser;
    private Review newReview;
    private FilmDbStorage filmStorage;
    private UserDbStorage userStorage;
    private ReviewStorage reviewStorage;

    @BeforeEach
    public void setup() {
        newFilm = new Film(1L, "film", "description", LocalDate.of(1991, 1, 1),
                120, Collections.emptyList(), Collections.emptyList(), new Mpa(1,"G"));
        newUser = new User(1L, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        newReview = new Review(1L,"review content", true, 1L, 1L, 0L);
        filmStorage = new FilmDbStorage(jdbcTemplate);
        userStorage = new UserDbStorage(jdbcTemplate);
        reviewStorage = new ReviewDbStorage(jdbcTemplate);
        userStorage.addUser(newUser);
        filmStorage.addFilm(newFilm);
    }

    @Test
    public void testAddReview() {
        Review savedReview = reviewStorage.addReview(newReview);

        assertThat(savedReview)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newReview);
    }

    @Test
    public void testGetReview() {
        reviewStorage.addReview(newReview);

        Review savedReview = reviewStorage.getReview(newReview.getFilmId());

        assertThat(savedReview)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newReview);
    }

    @Test
    public void testUpdateReview() {
        reviewStorage.addReview(newReview);
        newReview.setContent("new content");
        newReview.setIsPositive(false);

        Review savedReview = reviewStorage.updateReview(newReview);

        assertThat(savedReview)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newReview);
    }

    @Test
    public void testDeleteReview() {
        reviewStorage.addReview(newReview);
        Long filmId = newReview.getFilmId();

        Integer deletedReviewId = reviewStorage.deleteReview(1L);

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> {
                    reviewStorage.getReview(deletedReviewId);
                });
    }

    @Test
    public void testAddLikeReview() {
        reviewStorage.addReview(newReview);

        Review likedReview = reviewStorage.addLikeReview(newReview.getReviewId(), newReview.getUserId());

        Assertions.assertEquals(likedReview.getUseful(), 1, "Review liked");
    }

    @Test
    public void testAddDisLikeReview() {
        reviewStorage.addReview(newReview);

        Review dislikedReview = reviewStorage.addDislikeReview(newReview.getReviewId(), newReview.getUserId());

        Assertions.assertEquals(dislikedReview.getUseful(), -1, "Review disliked");
    }

    @Test
    public void testDeleteLikeReview() {
        reviewStorage.addReview(newReview);
        Review likedReview = reviewStorage.addLikeReview(newReview.getReviewId(), newReview.getUserId());

        Review unlikedReview =  reviewStorage.deleteLikeReview(likedReview.getReviewId(), likedReview.getUserId());

        Assertions.assertEquals(unlikedReview.getUseful(), 0, "Review unliked");
    }

    @Test
    public void testDeleteDislikeReview() {
        reviewStorage.addReview(newReview);
        Review dislikedReview = reviewStorage.addDislikeReview(newReview.getReviewId(), newReview.getUserId());

        Review undislikedReview =  reviewStorage.deleteLikeReview(dislikedReview.getReviewId(), dislikedReview.getUserId());

        Assertions.assertEquals(undislikedReview.getUseful(), -1, "Review undisliked");
    }
}
