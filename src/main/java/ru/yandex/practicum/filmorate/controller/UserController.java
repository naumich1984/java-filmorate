package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.RecommendationService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@Slf4j
public class UserController {

    //  private final UserStorage userStorage; нам не нужны стораджи в контроллерах сервис и так работает со сторадж
    private final UserService userService;
    private final RecommendationService recommendationService;

    @Autowired
    public UserController(UserService userService, RecommendationService recommendationService) {

        this.userService = userService;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/users")
    public ResponseEntity getAllUsers() {
        log.debug("GET /users request");

        return ResponseEntity.ok(userService.getUserStorage().getAllUsers());
    }

    @PostMapping("/users")
    public ResponseEntity addUser(@RequestBody @Valid @NotNull User user) {
        log.debug("POST /users request");

        return ResponseEntity.ok(userService.getUserStorage().addUser(user));
    }

    @PutMapping("/users")
    public ResponseEntity updateUser(@RequestBody @Valid @NotNull User user) {
        log.debug("PUT /users request");

        return ResponseEntity.ok(userService.getUserStorage().updateUser(user));
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("PUT /users/{id}/friends/{friendId} request");

        return ResponseEntity.ok(userService.addFriend(id, friendId));
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("DELETE /users/{id}/friends/{friendId} request");

        return ResponseEntity.ok(userService.deleteFriend(id, friendId));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity getUser(@PathVariable long id) {
        log.debug("GET /users/{id} request");

        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/users/{id}/friends")
    public ResponseEntity getUserFriends(@PathVariable long id) {
        log.debug("GET /users/{id}/friends request");

        return ResponseEntity.ok(userService.getUserFriends(id));
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public ResponseEntity findCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.debug("GET /users/{id}/friends/common/{otherId} request");

        return ResponseEntity.ok(userService.findCommonFriends(id, otherId));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity deleteUser(@PathVariable Long userId) {
        log.debug("DELETE /users/{userId}");

        return ResponseEntity.ok(userService.deleteUser(userId));
    }


    @GetMapping("/users/{id}/recommendations")
    public ResponseEntity getRecommendation(@PathVariable Long id) {
        log.info("GET /users/{userId}/recommendations");
        return ResponseEntity.ok(recommendationService.getRecommendedFilms(id));
    }
}
