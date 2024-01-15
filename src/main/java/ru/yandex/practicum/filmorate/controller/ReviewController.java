package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Validated
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public ResponseEntity<Review> addReview(@RequestBody @Valid Review review) {
        log.debug("POST /reviews request");

        return ResponseEntity.ok(reviewService.addReview(review));
    }

    @PutMapping("/reviews")
    public ResponseEntity<Review> updateReview(@RequestBody @Valid Review review) {
        log.debug("PUT /reviews request");

        return ResponseEntity.ok(reviewService.updateReview(review));
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Integer> deleteReview(@PathVariable Long id) {
        log.debug("DELETE /reviews/{id} request");
        log.debug("id: {}", id);

        return ResponseEntity.ok(reviewService.deleteReview(id));
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<Review> getReview(@PathVariable long id) {
        log.debug("GET /reviews/{id} request");
        log.debug("id: {}", id);

        return ResponseEntity.ok(reviewService.getReview(id));
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<Review>> getReviews(@RequestParam(required = false) Long filmId,
                                                   @RequestParam(required = false, defaultValue = "10") @Positive Long count) {
        log.debug("GET /reviews?filmId={filmId}&count={count}");
        log.debug("filmId: {} count: {}", filmId, count);

        return ResponseEntity.ok(reviewService.getReviews(filmId, count));
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public ResponseEntity<Review> addLikeToReview(@PathVariable long id, @PathVariable long userId) {
        log.debug("PUT /reviews/{id}/like/{userId}");
        log.debug("id: {} userId: {}", id, userId);

        return ResponseEntity.ok(reviewService.addLikeReview(id, userId));
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public ResponseEntity<Review> addDislikeToReview(@PathVariable long id, @PathVariable long userId) {
        log.debug("PUT /reviews/{id}/dislike/{userId}");
        log.debug("id: {} userId: {}", id, userId);

        return ResponseEntity.ok(reviewService.addDislikeReview(id, userId));
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public ResponseEntity<Review> deleteLikeToReview(@PathVariable long id, @PathVariable long userId) {
        log.debug("DELETE /reviews/{id}/like/{userId}");
        log.debug("id: {} userId: {}", id, userId);

        return ResponseEntity.ok(reviewService.deleteLikeReview(id, userId));
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public ResponseEntity<Review> deleteDislikeToReview(@PathVariable long id, @PathVariable long userId) {
        log.debug("DELETE /reviews/{id}/dislike/{userId}");
        log.debug("id: {} userId: {}", id, userId);

        return ResponseEntity.ok(reviewService.deleteDislikeReview(id, userId));
    }
}