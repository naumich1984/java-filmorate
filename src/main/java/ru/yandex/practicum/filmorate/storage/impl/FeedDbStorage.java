package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.FeedEventType;
import ru.yandex.practicum.filmorate.model.FeedOperations;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Feed> getUserFeed(Long userId) {
        log.debug("getUserFeed, userId {}", userId);
        String sql = "select f.id, " +
                "    f.create_time, " +
                "    f.user_id, " +
                "    et.event_type, " +
                "    o.operation, " +
                "    f.entity_id " +
                "    from feeds f inner join event_types et on et.id = f.event_type_id " +
                "    inner join operations o on o.id = f.operation_id " +
                "    inner join users u " +
                "    on u.id = f.user_id " +
                "    where u.id = ? " +
                "    order by f.id ";
        List<Feed> feeds = jdbcTemplate.query(sql, (rs, rowNum) -> getFeedMapper(rs), userId);
        if (feeds.isEmpty()) {
            return Collections.emptyList();
        }

        return feeds;
    }

    private Feed getFeedMapper(ResultSet rs) throws SQLException {
        log.debug("getUserMapper");
        return new Feed(rs.getLong("id"), rs.getTimestamp("create_time").toInstant().toEpochMilli(),
                rs.getLong("user_id"), rs.getString("event_type"),
                rs.getString("operation"), rs.getLong("entity_id"));
    }

    @Override
    public Integer addFeedEntity(long userId, FeedEventType feedEventType, FeedOperations feedOperation, long entityId) {
        int feedEventTypeId = 0;
        int feedOperationId = 0;
        Timestamp sysdate = Timestamp.valueOf(java.time.LocalDateTime.now());

        switch (feedEventType) {
            case LIKE: feedEventTypeId = 1;
                break;
            case REVIEW: feedEventTypeId = 2;
                break;
            case FRIEND: feedEventTypeId = 3;
                break;
        }
        switch (feedOperation) {
            case REMOVE: feedOperationId = 1;
                break;
            case ADD: feedOperationId = 2;
                break;
            case UPDATE: feedOperationId = 3;
                break;
        }
        String sqlQuery = "insert into feeds(create_time, user_id, event_type_id, operation_id, entity_id) " +
                " values(?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, sysdate, userId, feedEventTypeId, feedOperationId, entityId);

        return 0;
    }
}

