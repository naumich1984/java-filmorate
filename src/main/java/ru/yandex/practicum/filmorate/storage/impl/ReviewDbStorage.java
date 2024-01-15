package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component("reviewDbStorage")
@RequiredArgsConstructor
@Slf4j
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    private void existsReview(long filmId, long userId) {
        log.debug("existsReview, filmId {} userId {}", filmId, userId);
        String sql = " select " +
                "(select count(1) from reviews where film_id = ? and user_id = ?) review_exist, " +
                "(select count(1) from films where id = ?) film_exist, " +
                "(select count(1) from users where id = ?) user_exist " +
                "from dual ";
        List<List<Integer>> existsUser = jdbcTemplate.query(sql, (rs, rowNum) -> existsReviewMapper(rs),
                filmId, userId, filmId, userId);
        if (existsUser.get(0).get(0) == 1) {
            log.error("Review already exists!");
            throw new ValidationException("Review already exists!");
        }
        if (existsUser.get(0).get(1) == 0) {
            log.error("Film not found!");
            throw new NotFoundException("Film not found!");
        }
        if (existsUser.get(0).get(2) == 0) {
            log.error("User not found!");
            throw new NotFoundException("User not found!");
        }
    }

    private List<Integer> existsReviewMapper(ResultSet rs) throws SQLException {
        log.debug("existsReviewMapper");

        return List.of(rs.getInt(1), rs.getInt(2), rs.getInt(3));
    }

    private void validateReview(Review review) {
        if (review.getIsPositive() == null | review.getFilmId() == null | review.getUserId() == null) {
            throw new ValidationException("Review validate error!");
        }
    }

    @Override
    public Review addReview(Review review) {
        log.debug("addReview");
        validateReview(review);
        existsReview(review.getFilmId(), review.getUserId());
        String sqlQuery = "insert into reviews(film_id, user_id, review, is_positive) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setLong(1, review.getFilmId());
            stmt.setLong(2, review.getUserId());
            stmt.setString(3, review.getContent());
            stmt.setBoolean(4, review.getIsPositive());
            return stmt;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().longValue());

        return review;
    }

    @Override
    public Review updateReview(Review review) {
        log.debug("updateReview");
        validateReview(review);
        String sqlQuery = "update reviews set review = ?, is_positive = ? where id = ?";
        jdbcTemplate.update(sqlQuery, review.getContent(), review.getIsPositive(), review.getReviewId());

        return getReview(review.getReviewId());
    }

    @Override
    public Integer deleteReview(long id) {
        log.debug("deleteReview");
        String sql = "delete from reviews where id=?;";

        return jdbcTemplate.update(sql, id);
    }

    @Override
    public Review getReview(long id) {
        log.debug("getReview, filmId {}", id);
        String sql = "select r.id, " +
                " r.review, " +
                " r.is_positive, " +
                " r.film_id, " +
                " r.user_id, " +
                " nvl(sum(decode(re.is_useful, true, 1.0, false, -1.0)),0) useful " +
                " from reviews r left join review_estimation re on r.id = re.review_id " +
                " where r.id = ? " +
                " group by r.id, r.review, r.is_positive, r.film_id, r.user_id " +
                " order by 6 desc ";
        List<Review> reviews = jdbcTemplate.query(sql, (rs, rowNum) -> getReviewMapper(rs), id);
        if (reviews.isEmpty()) {
            throw new NotFoundException("Review not found!");
        }

        return reviews.get(0);
    }

    private Review getReviewMapper(ResultSet rs) throws SQLException {
        log.debug("getReviewMapper");

        return new Review(rs.getLong("id"), rs.getString("review"),
                rs.getBoolean("is_positive"), rs.getLong("film_id"),
                rs.getLong("user_id"), rs.getLong("useful"));
    }

    @Override
    public List<Review> getReviews(Long filmId, Long count) {
        long filmIdReview = Optional.ofNullable(filmId).orElse(-1L);
        log.debug("getReviews getReviews = {}, filmIdReviews = {} ", count, filmIdReview);
        String sql;
        if (filmIdReview < 0) {
            //Фильм не задан - отдаем все отзывы, сортировка по полезности
            sql = "select r.id, " +
                " r.review, " +
                " r.is_positive, " +
                " r.film_id, " +
                " r.user_id, " +
                " nvl(sum(decode(re.is_useful, true, 1.0, false, -1.0)),0) useful " +
                " from reviews r left join review_estimation re on r.id = re.review_id " +
                " where -1 = ? " +
                " group by r.id, r.review, r.is_positive, r.film_id, r.user_id " +
                " order by 6 desc " +
                " limit ? ";
        } else {
            //Фильм задан - отдаем только отзывы фильма, сортировка по полезности
            sql = "select r.id, " +
                    " r.review, " +
                    " r.is_positive, " +
                    " r.film_id, " +
                    " r.user_id, " +
                    " nvl(sum(decode(re.is_useful, true, 1.0, false, -1.0)),0) useful " +
                    " from reviews r left join review_estimation re on r.id = re.review_id " +
                    " where r.film_id = ? " +
                    " group by r.id, r.review, r.is_positive, r.film_id, r.user_id " +
                    " order by 6 desc " +
                    " limit ? ";
        }
        List<Review> reviews = jdbcTemplate.query(sql, (rs, rowNum) -> getReviewMapper(rs), filmIdReview, count);
        if (reviews.isEmpty()) {
            return Collections.emptyList();
        }

        return reviews;
    }

    @Override
    public Review addLikeReview(long id, long userId) {
        log.debug("addLikeReview");

        return addLikeDislikeReview(id, userId, true);
    }

    @Override
    public Review addDislikeReview(long id, long userId) {
        log.debug("addDislikeReview");

        return addLikeDislikeReview(id, userId, false);
    }

    private Review addLikeDislikeReview(long reviewId, long userId, boolean is_useful) {
        log.debug("addLikeDislikeReview");
        String sqlQuery = "insert into review_estimation(review_id, user_id, is_useful) " +
                "values (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setLong(1, reviewId);
            stmt.setLong(2, userId);
            stmt.setBoolean(3, is_useful);
            return stmt;
        }, keyHolder);

        return getReview(reviewId);
    }

    @Override
    public Review deleteLikeReview(long id, long userId) {
        log.debug("deleteLikeReview");

        return deleteLikeDislikeReview(id, userId, true);
    }

    @Override
    public Review deleteDislikeReview(long id, long userId) {
        log.debug("deleteDislikeReview");

        return deleteLikeDislikeReview(id, userId, false);
    }

    private Review deleteLikeDislikeReview(long reviewId, long userId, boolean is_useful) {
        String sqlQuery = "delete from review_estimation where review_id = ? and user_id = ? and is_useful = ? ";
        jdbcTemplate.update(sqlQuery, reviewId, userId, is_useful);

        return getReview(reviewId);
    }
}
