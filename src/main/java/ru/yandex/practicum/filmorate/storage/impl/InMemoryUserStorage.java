package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoUserFoundException;
import ru.yandex.practicum.filmorate.exception.NoUserLikesFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> usersRate = new HashMap<>();
    private Long idUserSequence = 1L;

    @Override
    public List<User> getAllUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public User addUser(User user) {
        log.debug(user.toString());
        User userValidated = validateUser(user);
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
        User userValidated = validateUser(user);
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
    public User getUser(Long userId) {
        Optional<User> userO = getUserById(userId);
        if (userO.isPresent()) {
            return userO.get();
        }
        throw new NoUserFoundException("User not found!");
    }

    @Override
    public List<User> getUserFriends(Long userId) {
        log.debug("UserId {}", userId);
        try {
            Set<Long> userFriends = usersRate.get(userId);

            return userFriends.stream()
                    .map(idUser -> getUser(idUser))
                    .collect(Collectors.toList());
        } catch (NoSuchElementException e) {
            throw new NoUserFoundException(e.getMessage());
        }
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        if (getUser(userId) == null) {
            throw new NoUserFoundException("User not found");
        }
        Set<Long> friendsList = Optional.ofNullable(usersRate.get(userId)).orElse(new HashSet<>());
        log.debug("Add like to list");
        if (getUserById(friendId).isPresent()) {
            friendsList.add(friendId);
            log.debug("Update film like-list");
            usersRate.put(userId, friendsList);

            Set<Long> friendFriendsList = Optional.ofNullable(usersRate
                    .get(friendId)).orElse(new HashSet<>());
            friendFriendsList.add(userId);
            usersRate.put(friendId, friendFriendsList);
        } else {
            throw new NoUserFoundException("Friend not found");
        }

        Optional<User> userO = getUserById(userId);
        if (userO.isPresent()) {
            return userO.get();
        }
        throw new NoUserFoundException("User not found!");
    }

    @Override
    public User deleteFriend(Long userId, Long friendId) {
       Optional<Set<Long>> friendsListO = Optional.ofNullable(usersRate.get(userId));
        if (!friendsListO.isPresent()) {
            throw new NoUserLikesFoundException("User " + userId + " did not have any friends");
        } else {
            Optional<Long> likeExistO = friendsListO.get().stream().filter(f -> f.longValue() == friendId).findFirst();
            if (likeExistO.isPresent()) {
                friendsListO.get().removeIf(uId -> uId.longValue() == friendId);
                usersRate.put(userId, friendsListO.get());

                Optional<Set<Long>> friendFriendsListO = Optional.ofNullable(usersRate.get(friendId));
                friendFriendsListO.get().removeIf(uId -> uId.longValue() == userId);
                usersRate.put(friendId, friendFriendsListO.get());
            } else {
                throw new NoUserLikesFoundException("User " + userId + " did not have friend " + friendId);
            }
        }

        Optional<User> userO = getUserById(userId);
        if (userO.isPresent()) {
            return userO.get();
        }
        throw new NoUserFoundException("User not found!");
    }

    private Optional<User> getUserById(Long userId) {
        return this.getAllUsers().stream().filter(f -> f.getId() == userId).findFirst();
    }

    private User validateUser(User user) {
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
