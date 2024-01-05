package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review addReview(Review review);

    Review updateReview(Review review);

    Integer deleteReview(long id);

    Review getReview(long id);

    List<Review> getReviews(Long filmId, Long count);

    Review addLikeReview(long id, long userId);

    Review addDislikeReview(long id, long userId);

    Review deleteLikeReview(long id, long userId);

    Review deleteDislikeReview(long id, long userId);
}
