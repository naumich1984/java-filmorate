package ru.yandex.practicum.filmorate.exception;

public class NoDirectorFoundException extends RuntimeException {
    public NoDirectorFoundException(String message) {
        super(message);
    }
}
