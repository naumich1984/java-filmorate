package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.RecommendationService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final RecommendationService recommendationService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        log.debug("GET /users request");

        return ResponseEntity.ok(userService.getUserStorage().getAllUsers());
    }

    @PostMapping("/users")
    public ResponseEntity<User> addUser(@RequestBody @Valid User user) {
        log.debug("POST /users request");

        return ResponseEntity.ok(userService.getUserStorage().addUser(user));
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody @Valid User user) {
        log.debug("PUT /users request");

        return ResponseEntity.ok(userService.getUserStorage().updateUser(user));
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("PUT /users/{id}/friends/{friendId} request");

        return ResponseEntity.ok(userService.addFriend(id, friendId));
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<User> deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("DELETE /users/{id}/friends/{friendId} request");

        return ResponseEntity.ok(userService.deleteFriend(id, friendId));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable long id) {
        log.debug("GET /users/{id} request");

        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/users/{id}/friends")
    public ResponseEntity<List<User>> getUserFriends(@PathVariable long id) {
        log.debug("GET /users/{id}/friends request");

        return ResponseEntity.ok(userService.getUserFriends(id));
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> findCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.debug("GET /users/{id}/friends/common/{otherId} request");

        return ResponseEntity.ok(userService.findCommonFriends(id, otherId));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        log.debug("DELETE /users/{userId}");

        return ResponseEntity.ok(userService.deleteUser(userId));
    }


    @GetMapping("/users/{id}/recommendations")
    public ResponseEntity<List<Film>> getRecommendation(@PathVariable Long id) {
        log.info("GET /users/{userId}/recommendations");
        return ResponseEntity.ok(recommendationService.getRecommendedFilms(id));
    }
}
