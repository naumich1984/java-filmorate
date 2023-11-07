package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer idUserSequence = 1;

    @GetMapping("/users")
    public ResponseEntity allUsers() {
        log.debug("GET /users request");

        return ResponseEntity.ok(users.values().stream().collect(Collectors.toList()));
    }

    @PostMapping("/users")
    public ResponseEntity addUser(@RequestBody @Valid @NotNull User user) {
        try {
            log.debug("POST /users request");
            log.debug(user.toString());
            User userValidated = validationUser(user);
            log.debug("add user validation success");
            if (users.values().stream().filter(u -> u.equals(user)).findFirst().isPresent()) {
                log.error("adding user already exists!");
                throw new ValidationException("Adding user already exists!");
            }
            userValidated.setId(idUserSequence++);
            log.debug("set user id {}", idUserSequence - 1);
            users.put(user.getId(), userValidated);
            log.debug("Put user into map");
            return ResponseEntity.ok(userValidated);
        } catch (ValidationException e) {
            log.error("add user error:{}", e.getMessage());
            log.error("add user trace:{}", e.getStackTrace());

            return ResponseEntity.badRequest().body(user);
        }
    }

    @PutMapping("/users")
    public ResponseEntity updateUser(@RequestBody @Valid @NotNull User user) {
        try {
            log.debug("PUT /users request");
            log.debug(user.toString());
            User userValidated = validationUser(user);
            log.debug("update user validation success");
            if (!users.keySet().stream().filter(k -> k.equals(userValidated.getId())).findFirst().isPresent()) {
                log.error("updating user not exists!");
                throw new ValidationException("updating user not exists!");
            }
            users.put(user.getId(), userValidated);
            log.debug("updating user in map");

            return ResponseEntity.ok(userValidated);
        } catch (ValidationException e) {
            log.error("update user error:{}", e.getMessage());
            log.error("update user trace:{}", e.getStackTrace());

            return ResponseEntity.internalServerError().body(user);
        }
    }

    private User validationUser(User user) {
        log.debug("validation user");
        Optional<User> userO = Optional.ofNullable(user);
        if (userO.isPresent()) {
            log.debug("check and repair user name");
            if (!Optional.ofNullable(userO.get().getName()).isPresent() || userO.get().getName().isBlank()) {
                userO.get().setName(userO.get().getLogin());
            }
            log.debug("set user name");
        } else {
            throw new ValidationException("adding user is null!");
        }

        return userO.get();
    }
}
