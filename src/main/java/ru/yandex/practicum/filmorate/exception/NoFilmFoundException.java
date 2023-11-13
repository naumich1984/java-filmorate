package ru.yandex.practicum.filmorate.exception;

public class NoFilmFoundException extends RuntimeException {
    public NoFilmFoundException(String message) {
        super(message);
    }
}
