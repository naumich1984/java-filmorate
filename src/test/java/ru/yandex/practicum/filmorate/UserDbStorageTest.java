package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private User newUser;
    private User newUser2;
    private UserDbStorage userStorage;

    @BeforeEach
    public void setup() {
        newUser = new User(1L, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        newUser2 = new User(2L, "user@email.ru", "vanya456", "Petr Ivanov", LocalDate.of(1990, 2, 2));
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    public void testFindUserById() {
        // Подготавливаем данные для теста
        userStorage.addUser(newUser);

        // вызываем тестируемый метод
        User savedUser = userStorage.getUser(1L);

        // проверяем утверждения
        assertThat(savedUser)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newUser);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testGetAllUsers() {
        // Подготавливаем данные для теста
        userStorage.addUser(newUser);

        // вызываем тестируемый метод
        User savedUser = userStorage.allUsers().get(0);

        // проверяем утверждения
        assertThat(savedUser)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newUser);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testAddUser() {
        // Подготавливаем данные для теста

        // вызываем тестируемый метод
        User savedUser = userStorage.addUser(newUser);

        // проверяем утверждения
        assertThat(savedUser)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newUser);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testUpdateUser() {
        // Подготавливаем данные для теста
        userStorage.addUser(newUser);
        newUser.setEmail("updated_user@email.ru");

        // вызываем тестируемый метод
        User savedUser = userStorage.updateUser(newUser);

        // проверяем утверждения
        assertThat(savedUser)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newUser);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testGetAllUserFriend() {
        // Подготавливаем данные для теста
        userStorage.addUser(newUser);
        userStorage.addUser(newUser2);
        userStorage.addFriend(1L, 2L);
        userStorage.addFriend(2L, 1L);

        // вызываем тестируемый метод
        User savedUser = userStorage.getUserFriends(1L).get(0);

        // проверяем утверждения
        assertThat(savedUser)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newUser2);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testAddUserFriend() {
        // Подготавливаем данные для теста
        userStorage.addUser(newUser);
        userStorage.addUser(newUser2);
        userStorage.addFriend(newUser2.getId(), newUser.getId());

        // вызываем тестируемый метод
        User savedUser = userStorage.addFriend(newUser.getId(), newUser2.getId());

        // проверяем утверждения
        assertThat(savedUser)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newUser2);        // и сохраненного пользователя - совпадают
    }

    @Test
    public void testDeleteUserFriend() {
        // Подготавливаем данные для теста
        userStorage.addUser(newUser);
        userStorage.addUser(newUser2);
        userStorage.addFriend(newUser.getId(), newUser2.getId());
        userStorage.addFriend(newUser2.getId(), newUser.getId());

        // вызываем тестируемый метод
        User savedUser = userStorage.deleteFriend(newUser.getId(), newUser2.getId());

        // проверяем утверждения
        assertThat(savedUser)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison() // проверяем, что значения полей нового
                .isEqualTo(newUser2);        // и сохраненного пользователя - совпадают
    }
}