package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoFriednshipConfimException;
import ru.yandex.practicum.filmorate.exception.NoUserFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component("userDbStorage")
@RequiredArgsConstructor
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAllUsers() {
        log.debug("getAllUsers");
        String sql = "select * from users";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> getUserMapper(rs), null);
        if (users.isEmpty()) {
            return Collections.emptyList();
        }

        return users;
    }

    @Override
    public User getUser(Long userId) {
        log.debug("getUser, userId {}", userId);
        String sql = "select * from users where id = ?";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> getUserMapper(rs), userId);
        if (users.isEmpty()) {
            return null;
        }

        return users.get(0);
    }

    private User getUserMapper(ResultSet rs) throws SQLException {
        log.debug("getUserMapper");
        return new User(rs.getLong("id"), rs.getString("email"),
                rs.getString("login"), rs.getString("name"),
                LocalDate.parse(rs.getString("birthday")));
    }

    @Override
    public User addUser(User user) {
        log.debug("addUser, userId {}", user.getId());
        existsUser(user.getId(), "adding user already exists!", 1);
        User userValidated = validateUser(user);
        String sqlQuery = "insert into users(email, login, name, birthday) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, userValidated.getEmail());
            stmt.setString(2, userValidated.getLogin());
            stmt.setString(3, userValidated.getName());
            stmt.setDate(4, Date.valueOf(userValidated.getBirthday()));
            return stmt;
        }, keyHolder);
        userValidated.setId(keyHolder.getKey().longValue());

        return userValidated;
    }

    @Override
    public User updateUser(User user) {
        log.debug("updateUser, userId {}", user.getId());
        existsUser(user.getId(), "updating user not exists!", 0);
        User userValidated = validateUser(user);
        String sqlQuery = "update users set " +
                "email = ?, login = ?, name = ? , birthday = ?" +
                "where id = ?";
        jdbcTemplate.update(sqlQuery, userValidated.getEmail(), userValidated.getLogin(), userValidated.getName(),
                userValidated.getBirthday(), userValidated.getId());

        return userValidated;
    }

    @Override
    public List<User> getUserFriends(Long userId) {
        log.debug("getUserFriends, userId {}", userId);
        String sql = "select uf.id, uf.email, uf.login, uf.name, uf.birthday from users uf where uf.id IN ( \n" +
                "SELECT f.FRIEND_ID\n" +
                "FROM users u INNER JOIN friends f ON u.ID = f.USER_ID\n" +
                "WHERE f.FRIENDSHIP_ID = 1 and u.ID = ?)";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> getUserMapper(rs), userId);
        if (users.isEmpty()) {
            Collections.emptyList();
        }

        return users;
    }

    private void existsUser(Long userID, String errorMessage, Integer countValue) {
        log.debug("existsUser, userId {}", userID);
        String sql = "select count(1) from users where id = ?";
        List<Integer> existsUser = jdbcTemplate.query(sql, (rs, rowNum) -> existsUserMapper(rs), userID);
        if (existsUser.get(0) == countValue) {
            log.error(errorMessage);
            throw new NoUserFoundException(errorMessage);
        }
    }

    private Integer existsUserMapper(ResultSet rs) throws SQLException {
        log.debug("existsUserMapper");

        return rs.getInt(1);
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        log.debug("addFriend, userId {}, friendId {}", userId, friendId);
        existsUser(userId, "User not found!", 0);
        existsUser(friendId, "Friend not found!", 0);

        String sql = "select friendship_id from friends where user_id = ? and friend_id = ? \n" +
                "union all select friendship_id * 100 from friends where friend_id = ? and user_id = ? ";
        List<Integer> existsFriendship = jdbcTemplate.query(sql, (rs, rowNum) -> existsUserMapper(rs),
                userId, friendId, userId, friendId);

        if (existsFriendship.isEmpty()) {
            // нет дружбы
            String sqlQuery = "insert into friends(user_id, friend_id, friendship_id) " +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlQuery, userId, friendId, 1);

            return getUser(friendId);
        } else if (existsFriendship.get(0) == 1) {
            //подтвержденная

            return getUser(friendId);
        } else if (existsFriendship.get(0) == 2) {
            //не подтвержденная со стороны друга

            throw new NoFriednshipConfimException("Friend did not confirm friendship");
        } else if (existsFriendship.get(0) == 100) {
            //подтвержденная

            return getUser(friendId);
        } else if (existsFriendship.get(0) == 200) {
            //не подтвержденная
            String sqlQuery = "update friend set " +
                    "friendship_id = 2" +
                    "where user_id = ? and friend_id = ? ";
            jdbcTemplate.update(sqlQuery, friendId, userId);

            return getUser(friendId);
        }

        throw new NoFriednshipConfimException("Error adding friendship!");
    }

    @Override
    public User deleteFriend(Long userId, Long friendId) {
        log.debug("deleteFriend, userId {}, friendId {}", userId, friendId);
        existsUser(userId, "User not found!", 0);
        existsUser(friendId, "Friend not found!", 0);
        String sql = "select id from friends where user_id = ? and friend_id = ? \n" +
                "union all select id from friends where friend_id = ? and user_id = ? ";
        List<Integer> existsFriendship = jdbcTemplate.query(sql, (rs, rowNum) -> existsUserMapper(rs),
                userId, friendId, userId, friendId);

        if (existsFriendship.isEmpty()) {
            // нет дружбы
            throw new NoFriednshipConfimException("Error adding friendship!");
        }
        String sqlQuery = "delete from friends where id = ? ";
        jdbcTemplate.update(sqlQuery, existsFriendship.get(0));

        return getUser(friendId);
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
