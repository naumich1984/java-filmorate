package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoUserFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> usersRate = new HashMap<>();
    private Long idUserSequence = 1L;

    @Override
    public List<User> allUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public User addUser(User user) {
        log.debug(user.toString());
        User userValidated = validationUser(user);
        log.debug("add user validation success");
        if (users.values().stream().filter(u -> u.equals(user)).findFirst().isPresent()) {
            log.error("adding user already exists!");
            throw new ValidationException("Adding user already exists!");
        }
        userValidated.setId(idUserSequence++);
        log.debug("set user id {}", user.getId());
        users.put(user.getId(), userValidated);
        log.debug("Put user into map");
        return userValidated;
    }

    @Override
    public User updateUser(User user) {
        log.debug(user.toString());
        User userValidated = validationUser(user);
        log.debug("update user validation success");
        if (!users.keySet().stream().filter(k -> k.equals(userValidated.getId())).findFirst().isPresent()) {
            log.error("updating user not exists!");
            throw new NoUserFoundException("updating user not exists!");
        }
        users.put(user.getId(), userValidated);
        log.debug("updating user in map");

        return userValidated;
    }

    @Override
    public Map<Long, Set<Long>> getUsersFriendsStorage() {
        return usersRate;
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
