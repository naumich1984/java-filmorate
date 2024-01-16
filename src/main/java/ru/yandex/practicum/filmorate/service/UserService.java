package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperations;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User addFriend(Long userId, Long friendId) {
        log.debug("userId {}, friendId {}", userId, friendId);
        log.debug("addFriend");
        User userResult = userStorage.addFriend(userId, friendId);
        if (userResult != null) {
            feedStorage.addFeedEntity(userId, FeedEventType.FRIEND, FeedOperations.ADD, friendId);
        }

        return userResult;
    }

    public User deleteFriend(Long userId, Long friendId) {
        log.debug("filmId {}, userId {}", userId, friendId);
        log.debug("deleteFriend");
        User userResult = userStorage.deleteFriend(userId, friendId);
        if (userResult != null) {
            feedStorage.addFeedEntity(userId, FeedEventType.FRIEND, FeedOperations.REMOVE, friendId);
        }

        return userResult;
    }

    public User getUser(Long userId) {
        log.debug("userId {}", userId);
        User user = userStorage.getUser(userId);
        if (user == null) throw new NotFoundException("User not found!");

        return user;
    }

    public List<User> getUserFriends(Long userId) {
        log.debug("UserId {}", userId);
        List<User> userFriends = userStorage.getUserFriends(userId);

        return userFriends;
    }

    public List<User> findCommonFriends(Long userId, Long otherId) {
        log.debug("UserId {}, OtherId {}", userId, otherId);
        try {
            Set<User> userFriends = new HashSet<User>(userStorage.getUserFriends(userId));
            Set<User> otherUserFriends = new HashSet<User>(userStorage.getUserFriends(otherId));
            Set<User> commonFriends = new HashSet<>(userFriends);
            commonFriends.retainAll(otherUserFriends);

            return commonFriends.stream().collect(Collectors.toList());
        } catch (NullPointerException e) {
            return Collections.EMPTY_LIST;
        }
    }

    public String deleteUser(Long userId) {
        int result = userStorage.deleteUser(userId);
        switch (result) {
            case 0: {
                log.debug("There is no user with id={}", userId);
                return "User with id = " + userId + " is not exist.";
            }
            case 1: {
                log.debug("User with id={} has been deleted successfully.", userId);
                return "User with id = " + userId + " has been deleted successfully\".";
            }
            default: {
                log.error("DELETED MORE THAN ONE USER!!!");
                throw new NotFoundException("Something went wrong!");
            }
        }
    }
}
