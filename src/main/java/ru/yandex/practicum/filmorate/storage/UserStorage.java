package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserStorage {
    List<User> allUsers();

    User addUser(User user);

    User updateUser(User user);

    Map<Long, Set<Long>> getUsersFriendsStorage();
}
