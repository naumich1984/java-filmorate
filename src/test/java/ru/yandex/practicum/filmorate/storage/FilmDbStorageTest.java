package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private Film newFilm;
    private User newUser;
    private Film newFilm2;
    private Genre firstGenre;
    private Mpa firstMpa;
    private FilmDbStorage filmStorage;
    private UserDbStorage userStorage;

    @BeforeEach
    public void setup() {
        firstGenre = new Genre(1,"Комедия");
        firstMpa = new Mpa(1,"G");
        newFilm = new Film(1L, "film", "description", LocalDate.of(1991, 1, 1),
                120, Collections.emptyList(), new Mpa(1,"G"));
        newFilm2 = new Film(2L, "film2", "description2", LocalDate.of(1992, 2, 2),
                122, Collections.emptyList(), new Mpa(1,"G"));
        newUser = new User(1L, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        filmStorage = new FilmDbStorage(jdbcTemplate);
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    public void testFindFilmById() {
        // Подготавливаем данные для теста
        filmStorage.addFilm(newFilm);

        // вызываем тестируемый метод
        Film savedFilm = filmStorage.getFilm(1L);

        // проверяем утверждения
        assertThat(savedFilm)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newFilm);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testGetAllFilms() {
        // Подготавливаем данные для теста
        filmStorage.addFilm(newFilm);

        // вызываем тестируемый метод
        Film savedFilm = filmStorage.getAllFilms().get(0);

        // проверяем утверждения
        assertThat(savedFilm)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newFilm);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testAddFilm() {
        // Подготавливаем данные для теста

        // вызываем тестируемый метод
        Film savedFilm = filmStorage.addFilm(newFilm);

        // проверяем утверждения
        assertThat(savedFilm)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newFilm);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testUpdateFilm() {
        // Подготавливаем данные для теста
        filmStorage.addFilm(newFilm);
        newFilm.setName("film_updated");

        // вызываем тестируемый метод
        Film savedFilm = filmStorage.updateFilm(newFilm);

        // проверяем утверждения
        assertThat(savedFilm)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newFilm);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testGetTopNfilms() {
        // Подготавливаем данные для теста
        filmStorage.addFilm(newFilm);
        userStorage.addUser(newUser);

        filmStorage.addLikeToFilm(1L,1L);

        // вызываем тестируемый метод
        Film savedFilm = filmStorage.getTopNfilms(10).get(0);

        // проверяем утверждения
        assertThat(savedFilm)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newFilm);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testGetAllGenres() {
        // Подготавливаем данные для теста

        // вызываем тестируемый метод
        Genre genre = filmStorage.getAllGenres().get(0);

        // проверяем утверждения
        assertThat(genre)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(firstGenre);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testGetAllMpa() {
        // Подготавливаем данные для теста

        // вызываем тестируемый метод
        Mpa mpa = filmStorage.getAllMpa().get(0);

        // проверяем утверждения
        assertThat(mpa)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(firstMpa);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testAddLikeToFilm() {
        // Подготавливаем данные для теста
        filmStorage.addFilm(newFilm);
        userStorage.addUser(newUser);

        // вызываем тестируемый метод
        Film savedFilm =  filmStorage.addLikeToFilm(1L,1L);

        // проверяем утверждения
        assertThat(savedFilm)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(filmStorage.getTopNfilms(1).get(0));        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testDeleteLikeFromFilm() {
        // Подготавливаем данные для теста
        filmStorage.addFilm(newFilm);
        filmStorage.addFilm(newFilm2);
        userStorage.addUser(newUser);
        filmStorage.addLikeToFilm(1L,1L);
        filmStorage.addLikeToFilm(2L,1L);

        // вызываем тестируемый метод
        filmStorage.deleteLikeFromFilm(1L,1L);

        // проверяем утверждения
        assertThat(newFilm2)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(filmStorage.getTopNfilms(1).get(0));        // и сохраненного пользователя - совпадают
    }
}
