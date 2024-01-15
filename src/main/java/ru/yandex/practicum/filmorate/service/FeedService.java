package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@Data
public class FeedService {

    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    @Autowired
    public FeedService(UserStorage userStorage, FeedStorage feedStorage) {
        this.userStorage = userStorage;
        this.feedStorage = feedStorage;
    }

    public List<Feed> getUserFeed(Long userId) {
        log.debug("getUserFeed");
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new NotFoundException("User not found! Can`t get feeds!");
        }

        return feedStorage.getUserFeed(userId);
    }


}
