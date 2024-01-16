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

import java.util.List;

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
        log.debug("findCommonFriends");

        return userStorage.findCommonFriends(userId, otherId);
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
