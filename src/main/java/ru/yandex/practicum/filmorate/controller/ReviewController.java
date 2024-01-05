package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    public ResponseEntity addReview(@RequestBody @Valid @NotNull Review review) {
        log.debug("POST /reviews request");

        return ResponseEntity.ok(reviewService.getReviewStorage().addReview(review));
    }

    @PutMapping("/reviews")
    public ResponseEntity updateReview(@RequestBody @Valid @NotNull Review review) {
        log.debug("PUT /reviews request");

        return ResponseEntity.ok(reviewService.getReviewStorage().updateReview(review));
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity deleteReview(@PathVariable Long id) {
        log.debug("DELETE /reviews/{id} request");

        return ResponseEntity.ok(reviewService.getReviewStorage().deleteReview(id));
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity getGenre(@PathVariable long id) {
        log.debug("GET /reviews/{id} request");

        return ResponseEntity.ok(reviewService.getReviewStorage().getReview(id));
    }

    @GetMapping("/reviews")
    public ResponseEntity getReviews(@RequestParam(required = false) Long filmId,
                                     @RequestParam(required = false) Long count) {
        log.debug("GET /reviews?filmId={filmId}&count={count}");

        return ResponseEntity.ok(reviewService.getReviewStorage().getReviews(filmId, count));
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public ResponseEntity addLikeToReview(@PathVariable long id, @PathVariable long userId) {
        log.debug("PUT /reviews/{id}/like/{userId}");

        return ResponseEntity.ok(reviewService.getReviewStorage().addLikeReview(id, userId));
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public ResponseEntity addDislikeToReview(@PathVariable long id, @PathVariable long userId) {
        log.debug("PUT /reviews/{id}/dislike/{userId}");

        return ResponseEntity.ok(reviewService.getReviewStorage().addDislikeReview(id, userId));
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public ResponseEntity deleteLikeToReview(@PathVariable long id, @PathVariable long userId) {
        log.debug("DELETE /reviews/{id}/like/{userId}");

        return ResponseEntity.ok(reviewService.getReviewStorage().deleteLikeReview(id, userId));
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public ResponseEntity deleteDislikeToReview(@PathVariable long id, @PathVariable long userId) {
        log.debug("DELETE /reviews/{id}/dislike/{userId}");

        return ResponseEntity.ok(reviewService.getReviewStorage().deleteDislikeReview(id, userId));
    }
}