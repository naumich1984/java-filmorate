package ru.yandex.practicum.filmorate.controller;

public class ValidationException extends RuntimeException {
    ValidationException(String message) {
        super(message);
    }
}
