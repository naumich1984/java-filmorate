package ru.yandex.practicum.filmorate.exception;

public class NoUserLikesFoundException extends RuntimeException {
    public NoUserLikesFoundException(String message) {
        super(message);
    }
}