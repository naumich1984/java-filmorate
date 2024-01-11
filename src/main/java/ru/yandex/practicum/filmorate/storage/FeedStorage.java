package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperations;

import java.util.List;

public interface FeedStorage {

    List<Feed> getUserFeed(Long userId);

    Integer addFeedEntity(long userId, FeedEventType feedEventType, FeedOperations feedOperation, long entityId);
}
