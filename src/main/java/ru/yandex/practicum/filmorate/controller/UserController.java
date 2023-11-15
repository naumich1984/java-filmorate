package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@Slf4j
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity allUsers() {
        log.debug("GET /users request");

        return ResponseEntity.ok(userStorage.allUsers());
    }

    @PostMapping("/users")
    public ResponseEntity addUser(@RequestBody @Valid @NotNull User user) {
        log.debug("POST /users request");

        return ResponseEntity.ok(userStorage.addUser(user));
    }

    @PutMapping("/users")
    public ResponseEntity updateUser(@RequestBody @Valid @NotNull User user) {
        log.debug("PUT /users request");

        return ResponseEntity.ok(userStorage.updateUser(user));
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
}
