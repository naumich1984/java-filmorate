package ru.yandex.practicum.filmorate.exception;

public class NoReviewFoundException extends RuntimeException {
    public NoReviewFoundException(String message) {
        super(message);
    }
}
