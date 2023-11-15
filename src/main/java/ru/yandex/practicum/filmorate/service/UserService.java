package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoUserFoundException;
import ru.yandex.practicum.filmorate.exception.NoUserLikesFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(Long userId, Long friendId) {
        log.debug("userId {}, friendId {}", userId, friendId);
        log.debug("Get list likes");
        if (!userStorage.allUsers().stream().filter(f -> f.getId() == userId).findFirst().isPresent()) {
            throw new NoUserFoundException("User not found");
        }
        Set<Long> friendsList = Optional.ofNullable(userStorage.getUsersFriendsStorage().get(userId)).orElse(new HashSet<>());
        log.debug("Add like to list");
        if (userStorage.allUsers().stream().filter(f -> f.getId() == friendId).findFirst().isPresent()) {
            friendsList.add(friendId);
            log.debug("Update film like-list");
            userStorage.getUsersFriendsStorage().put(userId, friendsList);

            Set<Long> friendFriendsList = Optional.ofNullable(userStorage.getUsersFriendsStorage()
                    .get(friendId)).orElse(new HashSet<>());
            friendFriendsList.add(userId);
            userStorage.getUsersFriendsStorage().put(friendId, friendFriendsList);
        } else {
            throw new NoUserFoundException("Friend not found");
        }

        return userStorage.allUsers().stream().filter(f -> f.getId() == userId).findFirst().get();
    }

    public User deleteFriend(Long userId, Long friendId) {
        log.debug("filmId {}, userId {}", userId, friendId);
        Optional<Set<Long>> friendsListO = Optional.ofNullable(userStorage.getUsersFriendsStorage().get(userId));
        if (!friendsListO.isPresent()) {
            throw new NoUserLikesFoundException("User " + userId + " did not have any friends");
        } else {
            Optional<Long> likeExistO = friendsListO.get().stream().filter(f -> f.longValue() == friendId).findFirst();
            if (likeExistO.isPresent()) {
                friendsListO.get().removeIf(uId -> uId.longValue() == friendId);
                userStorage.getUsersFriendsStorage().put(userId, friendsListO.get());

                Optional<Set<Long>> friendFriendsListO = Optional.ofNullable(userStorage.getUsersFriendsStorage().get(friendId));
                friendFriendsListO.get().removeIf(uId -> uId.longValue() == userId);
                userStorage.getUsersFriendsStorage().put(friendId, friendFriendsListO.get());
            } else {
                throw new NoUserLikesFoundException("User " + userId + " did not have friend " + friendId);
            }
        }

        return userStorage.allUsers().stream().filter(f -> f.getId() == userId).findFirst().get();
    }

    public User getUser(Long userId) {
        log.debug("filmId {}", userId);
        try {
            return userStorage.allUsers().stream().filter(f -> f.getId() == userId).findFirst().get();
        } catch(NoSuchElementException e) {
            throw new NoUserFoundException(e.getMessage());
        }
    }

    public List<User> getUserFriends(Long userId) {
        log.debug("UserId {}", userId);
        try {
            Set<Long> userFriends = userStorage.getUsersFriendsStorage().get(userId);

            return userFriends.stream()
                    .map(idUser -> getUser(idUser))
                    .collect(Collectors.toList());
        } catch(NoSuchElementException e) {
            throw new NoUserFoundException(e.getMessage());
        }
    }

    public List<User> findCommonFriends(Long userId, Long otherId) {
        log.debug("UserId {}, OtherId {}", userId, otherId);
        try {
            Set<Long> userFriends = userStorage.getUsersFriendsStorage().get(userId);
            Set<Long> otherUserFriends = userStorage.getUsersFriendsStorage().get(otherId);
            Set<Long> commonFriends = new HashSet<>(userFriends);
            commonFriends.retainAll(otherUserFriends);

            return commonFriends.stream()
                    .map(idUser -> getUser(idUser))
                    .collect(Collectors.toList());
        } catch(NoSuchElementException | NullPointerException e) {
            return Collections.EMPTY_LIST;
        }
    }

}
