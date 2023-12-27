package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user);

    User getUser(Long userId);

    List<User> getUserFriends(Long userId);

    User addFriend(Long userId, Long friendId);

    User deleteFriend(Long userId, Long friendId);

    Integer deleteUser(Long userId);
}
