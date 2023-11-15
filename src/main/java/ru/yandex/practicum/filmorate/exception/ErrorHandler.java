package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Film failed validation")
    public Map<String, String> handleValidationException(final ValidationException e) {
        log.debug("Ошибка валидации:{}", e.getMessage());
        log.debug("stacktrace ошибки:{}", e.getStackTrace());

        return Map.of("Ошибка валидации",e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Film not found")
    public Map<String, String> handleNoFilmFoundException(final NoFilmFoundException e) {
        log.debug("Ошибка поиска:{}", e.getMessage());
        log.debug("stacktrace ошибки:{}", e.getStackTrace());

        return Map.of("Ошибка поиска фильма", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "InternalServerException")
    public Map<String, String> handleInternalServerException(final NullPointerException e) {
        log.debug("Ошибка сервера:{}", e.getMessage());
        log.debug("stacktrace ошибки:{}", e.getStackTrace());

        return Map.of("Ошибка сервера", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "No user likes found")
    public Map<String, String> handleNoUserLikesFoundException(final NoUserLikesFoundException e) {
        log.debug("Ошибка поиска лайка:{}", e.getMessage());
        log.debug("stacktrace ошибки:{}", e.getStackTrace());

        return Map.of("Ошибка поиска лайка", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "No user found")
    public Map<String, String> handleNoUserFoundException(final NoUserFoundException e) {
        log.debug("Ошибка поиска пользователя:{}", e.getMessage());
        log.debug("stacktrace ошибки:{}", e.getStackTrace());

        return Map.of("Ошибка поиска пользователя", e.getMessage());
    }

}
