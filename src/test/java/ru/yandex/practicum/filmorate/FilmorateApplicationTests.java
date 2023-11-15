package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {

    @Test
    void mockTest(){

    }

	private Validator validator;
	private UserController userController;
	private FilmController filmController;
	private User user;
	private Film film;
	private ResponseEntity userResponse;
	private ResponseEntity filmResponse;

	@BeforeEach
	public void setup() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
        UserStorage userStorage = new InMemoryUserStorage();
        FilmStorage filmStorage = new InMemoryFilmStorage();
		userController = new UserController(userStorage, new UserService(userStorage));
		filmController = new FilmController(filmStorage, new FilmService(filmStorage));
		user = User.builder()
				.id(null)
				.email("email@email.ru")
				.login("login")
				.name("name")
				.birthday(LocalDate.parse("1999-09-09"))
				.build();
		film = Film.builder()
				.id(null)
				.name("name")
				.description("description")
				.releaseDate(LocalDate.parse("1969-03-03"))
				.duration(123)
				.build();
		userResponse = userController.addUser(user);
		filmResponse = filmController.addFilm(film);
	}

	//---User Tests----------------------------------------------------------------------
	@Test
	void shouldGetAllUsers_whenGetRequest() {
		userResponse = userController.allUsers();

		assertEquals(userResponse.getStatusCode().value(), 200);
		assertTrue(userResponse.getBody().toString().contains(user.toString()));
	}

	@Test
	void shouldAddUser_whenPostCorrectUser() {
		assertEquals(userResponse.getStatusCode().value(), 200);
	}

	@Test
	void shouldViolationAddUser_whenPostUserWithIncorrectEmail() {
		user.setEmail("email.email.ru");

		Set<ConstraintViolation<User>> violations = validator.validate(user);

		assertFalse(violations.isEmpty());
		assertEquals("email should exists @ symbol", violations.stream().findFirst().get().getMessageTemplate());
	}

	@Test
	void shouldViolationAddUser_whenPostUserWithIncorrectLogin() {
		user.setLogin("login login");

		Set<ConstraintViolation<User>> violations = validator.validate(user);

		assertFalse(violations.isEmpty());
		assertEquals("login should not exists space", violations.stream().findFirst().get().getMessageTemplate());
	}

	@Test
	void shouldViolationAddUser_whenPostUserWithEmptyLogin() {
		user.setLogin("");

		Set<ConstraintViolation<User>> violations = validator.validate(user);

		assertFalse(violations.isEmpty());
		assertEquals("login should not be blank", violations.stream()
				.filter(f -> f.getMessageTemplate().contains("login should not be blank"))
				.findFirst()
				.get().getMessageTemplate());
	}

	@Test
	void shouldViolationAddUser_whenPostUserWithLoginNull() {
		user.setLogin(null);

		Set<ConstraintViolation<User>> violations = validator.validate(user);

		assertFalse(violations.isEmpty());
		assertEquals("login should not null", violations.stream()
				.filter(f -> f.getMessageTemplate().contains("login should not null"))
				.findFirst()
				.get().getMessageTemplate());
	}

	@Test
	void shouldAddUserAndSetLoginToName_whenPostUserWithEmptyName() {
		User user2 = User.builder().email("email@email2.ru").login("login2").build();

		ResponseEntity userResponse = userController.addUser(user2);

		assertEquals(userResponse.getStatusCode().value(), 200);
		assertTrue(userResponse.getBody().toString().contains("name=login"));
	}

	@Test
	void shouldViolationAddUser_whenPostUserBirthdayInFuture() {
		user.setBirthday(LocalDate.parse("2222-12-12"));

		Set<ConstraintViolation<User>> violations = validator.validate(user);

		assertFalse(violations.isEmpty());
		assertEquals("birthday should be in past", violations.stream().findFirst().get().getMessageTemplate());
	}

	@Test
	void shouldUpdateUser() {
		user.setName("updated");

		ResponseEntity userResponse = userController.updateUser(user);

		assertEquals(userResponse.getStatusCode().value(), 200);
		assertTrue(userResponse.getBody().toString().contains("name=updated"));
	}

	//---Film Tests----------------------------------------------------------------------
	@Test
	void shouldGetAllFilms_whenGetRequest() {
		filmResponse = filmController.allFilms();

		assertEquals(filmResponse.getStatusCode().value(), 200);
		assertTrue(filmResponse.getBody().toString().contains(film.toString()));
	}

	@Test
	void shouldAddFilm_whenPostCorrectFilm() {
		assertEquals(filmResponse.getStatusCode().value(), 200);
	}

	@Test
	void shouldViolationAddFilm_whenPostFilmWithEmptyName() {
		film.setName("");

		Set<ConstraintViolation<Film>> violations = validator.validate(film);

		assertFalse(violations.isEmpty());
		assertEquals("name should not be blank", violations.stream().findFirst().get().getMessageTemplate());
	}

	@Test
	void shouldViolationAddFilm_whenPostFilmWithDescriptionLengthOver200symbols() {
		film.setDescription("123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
				"1234567891123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
				"1234567891123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891");

		Set<ConstraintViolation<Film>> violations = validator.validate(film);

		assertFalse(violations.isEmpty());
		assertEquals("description length should be less 200 symbols", violations.stream()
				.findFirst()
				.get()
				.getMessageTemplate());
	}

	@Test
	void shouldFilmUser_whenPostFilmWithDescriptionLength200symbols() {
		film.setDescription("123456789112345678911234567891123456789112345678911234567891123456789112345678911234567891" +
				"12345678911234567891123456789112345678911234567891123456789112345678911234567891" +
				"123456789112345678911234567891");

		Set<ConstraintViolation<Film>> violations = validator.validate(film);

		assertTrue(violations.isEmpty());
	}

	@Test
	void shouldViolationAddFilm_whenPostFilmWithReleaseDateBefore1895() {
		Film film2 = Film.builder()
				.id(null)
				.name("name2")
				.description("description2")
				.duration(120)
				.releaseDate(LocalDate.parse("1805-12-28"))
				.build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    ResponseEntity filmResponse = filmController.addFilm(film2);
                });
	}

	@Test
	void shouldViolationAddFilm_whenPostFilmWithDurationNegative() {
		film.setDuration(-1);

		Set<ConstraintViolation<Film>> violations = validator.validate(film);

		assertFalse(violations.isEmpty());
		assertEquals("duration should be positive", violations.stream()
				.filter(f -> f.getMessageTemplate().contains("duration should be positive"))
				.findFirst()
				.get().getMessageTemplate());
	}

	@Test
	void shouldViolationAddFilm_whenPostFilmWithDurationZero() {
		film.setDuration(0);

		Set<ConstraintViolation<Film>> violations = validator.validate(film);

		assertFalse(violations.isEmpty());
		assertEquals("duration should be positive", violations.stream()
				.filter(f -> f.getMessageTemplate().contains("duration should be positive"))
				.findFirst()
				.get().getMessageTemplate());
	}

	@Test
	void shouldUpdateFilm() {
		film.setName("updated film");

		ResponseEntity filmResponse = filmController.updateFilm(film);

		assertEquals(filmResponse.getStatusCode().value(), 200);
		assertTrue(filmResponse.getBody().toString().contains("name=updated film"));
	}
}
