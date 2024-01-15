package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NoFriednshipConfimException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e){
        log.debug("Ошибка валидации:{}", e.getMessage());
        log.debug("stacktrace ошибки:{}", e.getStackTrace());

        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Film failed validation")
    public Map<String, String> handleValidationException(final ValidationException e) {
        log.debug("Ошибка валидации:{}", e.getMessage());
        log.debug("stacktrace ошибки:{}", e.getStackTrace());

        return Map.of("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Not found!")
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        log.debug("Ошибка:{}", e.getMessage());
        log.debug("stacktrace ошибки:{}", e.getStackTrace());

        return Map.of("Ошибка:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "InternalServerException")
    public Map<String, String> handleInternalServerException(final NullPointerException e) {
        log.debug("Ошибка сервера:{}", e.getMessage());
        log.debug("stacktrace ошибки:{}", e.getStackTrace());

        return Map.of("Ошибка сервера", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Friendship not found")
    public Map<String, String> handleNoFriednshipConfimException(final NoFriednshipConfimException e) {
        log.debug("Ошибка поиска дружбы:{}", e.getMessage());
        log.debug("stacktrace ошибки:{}", e.getStackTrace());

        return Map.of("Ошибка поиска дружбы", e.getMessage());
    }
}
