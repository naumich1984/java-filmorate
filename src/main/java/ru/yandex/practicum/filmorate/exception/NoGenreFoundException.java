package ru.yandex.practicum.filmorate.exception;

public class NoGenreFoundException extends RuntimeException {
    public NoGenreFoundException(String message) {
        super(message);
    }
}
