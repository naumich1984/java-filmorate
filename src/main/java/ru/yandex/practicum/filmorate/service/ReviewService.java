package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperations;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Service
@Slf4j
@Data
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FeedStorage feedStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, FeedStorage feedStorage, UserStorage userStorage) {
        this.reviewStorage = reviewStorage;
        this.feedStorage = feedStorage;
    }

    public Review addReview(Review review) {
        Review reviewResult = reviewStorage.addReview(review);
        feedStorage.addFeedEntity(review.getUserId(), FeedEventType.REVIEW, FeedOperations.ADD, review.getReviewId());

        return reviewResult;
    }

    public Review updateReview(Review review) {
        Review reviewResult = reviewStorage.updateReview(review);
        feedStorage.addFeedEntity(reviewResult.getUserId(), FeedEventType.REVIEW, FeedOperations.UPDATE, review.getReviewId());

        return reviewResult;
    }

    public Integer deleteReview(long reviewId) {
        Review deletedReview = reviewStorage.getReview(reviewId);
        Integer reviewResult = reviewStorage.deleteReview(reviewId);
        feedStorage.addFeedEntity(deletedReview.getUserId(), FeedEventType.REVIEW, FeedOperations.REMOVE, reviewId);

        return reviewResult;
    }

    public Review addLikeReview(long id, long userId) {
        Review reviewResult = reviewStorage.addLikeReview(id, userId);
        feedStorage.addFeedEntity(userId, FeedEventType.LIKE, FeedOperations.ADD, id);

        return reviewResult;
    }

    public Review deleteLikeReview(long id, long userId) {
        Review reviewResult = reviewStorage.deleteLikeReview(id, userId);
        feedStorage.addFeedEntity(userId, FeedEventType.LIKE, FeedOperations.REMOVE, id);

        return reviewResult;
    }
}
