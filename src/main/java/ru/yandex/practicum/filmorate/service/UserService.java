package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoUserFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Data
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(Long userId, Long friendId) {
        log.debug("userId {}, friendId {}", userId, friendId);
        log.debug("Get list likes");
        return userStorage.addFriend(userId, friendId);
    }

    public User deleteFriend(Long userId, Long friendId) {
        log.debug("filmId {}, userId {}", userId, friendId);
        return userStorage.deleteFriend(userId, friendId);
    }

    public User getUser(Long userId) {
        log.debug("userId {}", userId);
        try {
            User user = userStorage.getUser(userId);
            if (user == null) throw new NoUserFoundException("User not found!");
            return user;
        } catch (NoSuchElementException e) {
            throw new NoUserFoundException(e.getMessage());
        }
    }

    public List<User> getUserFriends(Long userId) {
        log.debug("UserId {}", userId);
        try {
            List<User> userFriends = userStorage.getUserFriends(userId);
            return userFriends;
        } catch (NoSuchElementException e) {
            throw new NoUserFoundException(e.getMessage());
        }
    }

    public List<User> findCommonFriends(Long userId, Long otherId) {
        log.debug("UserId {}, OtherId {}", userId, otherId);
        try {
            Set<User> userFriends = new HashSet<User>(userStorage.getUserFriends(userId));
            Set<User> otherUserFriends = new HashSet<User>(userStorage.getUserFriends(otherId));
            Set<User> commonFriends = new HashSet<>(userFriends);
            commonFriends.retainAll(otherUserFriends);

            return commonFriends.stream().collect(Collectors.toList());
        } catch (NoSuchElementException | NullPointerException e) {
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
                throw new NoUserFoundException("Something went wrong!");
            }
        }
    }


}
