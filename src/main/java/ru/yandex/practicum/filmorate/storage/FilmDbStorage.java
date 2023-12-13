package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoFilmFoundException;
import ru.yandex.practicum.filmorate.exception.NoUserFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final LocalDate minDateRelease = LocalDate.parse("1895-12-28");
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> allFilms() {
        log.debug("allFilms");
        String sql = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, \n" +
                "(select string_agg(genre_id::text, ',') from GENRES_FILMS gf WHERE f.ID = gf.FILM_ID) GENRES, f.MPA_ID,\n" +
                "(select m.mpa_name from mpa m where m.id = f.MPA_ID) mpa_name FROM FILMS f ";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> getFilmMapper(rs), null);
        if (films.isEmpty()) {
            return Collections.emptyList();
        }

        return films;
    }

    private Film getFilmMapper(ResultSet rs) throws SQLException {
        log.debug("getFilmMapper");
        List<Genre> genresList;
        if (rs.getString("genres") != null) {
            genresList = Arrays.stream(Optional.ofNullable(rs.getString("genres")).orElse("").split(","))
                    .map(f -> allGenres().stream().filter(g -> g.getId().equals(Integer.parseInt(f))).findFirst().get())
                    .collect(Collectors.toList());
        } else {
            genresList = Collections.emptyList();
        }

        genresList.sort((o1, o2) -> o1.getId() - o2.getId());
        return new Film(rs.getLong("id"), rs.getString("name"),
                rs.getString("description"), LocalDate.parse(rs.getString("release_date")),
                rs.getInt("duration"), genresList
                , new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
    }

    @Override
    public Film addFilm(Film film) {
        log.debug("addFilm, filmId {}", film.getId());
        existsFilm(film.getId(), "adding film already exists!", 1);
        validationFilm(film);
        String sqlQuery = "insert into films(name, description, release_date, duration, mpa_id) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());

        if (film.getGenres() != null) {
            Long filmId = film.getId();
            Set<Genre> uniqueGenres = new HashSet<Genre>(film.getGenres());
            List<Genre> GenresList = new ArrayList<>();
            GenresList.addAll(uniqueGenres);
            GenresList.sort((o1, o2) -> o2.getId() - o1.getId());
            for (Genre genre : uniqueGenres) {
                String sqlQueryGenres = "insert into genres_films(film_id, genre_id) " +
                        "values (?, ?)";
                jdbcTemplate.update(sqlQueryGenres, filmId, genre.getId());
            }
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("updateFilm, filmId {}", film.getId());
        existsFilm(film.getId(), "updating film not exists!", 0);
        validationFilm(film);
        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ? , duration = ?, mpa_id = ?" +
                "where id = ?";
        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId());

        sqlQuery = "delete from genres_films where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());

        List<Genre> GenresList = new ArrayList<>();
        if (film.getGenres() != null) {
            Long filmId = film.getId();
            Set<Genre> uniqueGenres = new HashSet<Genre>(film.getGenres());
            GenresList.addAll(uniqueGenres);
            for (Genre genre : uniqueGenres) {
                String sqlQueryGenres = "insert into genres_films(film_id, genre_id) " +
                        "values (?, ?)";
                jdbcTemplate.update(sqlQueryGenres, filmId, genre.getId());
            }
        }
        film.setGenres(GenresList);
        return film;
    }

    @Override
    public List<Genre> allGenres() {
        log.debug("allGenres");
        String sql = "select * from genres";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> getGenreMapper(rs), null);
        if (genres.isEmpty()) {
            return Collections.emptyList();
        }

        return genres;
    }

    private Genre getGenreMapper(ResultSet rs) throws SQLException {
        log.debug("getGenreMapper");
        return new Genre(rs.getInt("id"), rs.getString("genre_name"));
    }

    @Override
    public List<Mpa> allMpa() {
        log.debug("allMpa");
        String sql = "select * from mpa";
        List<Mpa> mpaList = jdbcTemplate.query(sql, (rs, rowNum) -> getMpaMapper(rs), null);
        if (mpaList.isEmpty()) {
            return Collections.emptyList();
        }

        return mpaList;
    }

    private Mpa getMpaMapper(ResultSet rs) throws SQLException {
        log.debug("getMpaMapper");
        return new Mpa(rs.getInt("id"), rs.getString("mpa_name"));
    }

    private void existsFilm(Long userID, String errorMessage, Integer countValue) {
        log.debug("existsFilm");
        String sql = "select count(1) from films where id = ?";
        List<Integer> existsFilm = jdbcTemplate.query(sql, (rs, rowNum) -> existsFilmMapper(rs), userID);
        if (existsFilm.get(0) == countValue) {
            log.error(errorMessage);
            throw new NoUserFoundException(errorMessage);
        }
    }

    private Integer existsFilmMapper(ResultSet rs) throws SQLException {
        log.debug("existsFilmMapper");
        return rs.getInt(1);
    }

    @Override
    public Film getFilm(Long filmId) {
        log.debug("getFilm, filmId {}", filmId);
        String sql = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, \n" +
                "(select string_agg(genre_id::text, ',') from GENRES_FILMS gf WHERE f.ID = gf.FILM_ID) GENRES, f.MPA_ID,\n" +
                "(select m.mpa_name from mpa m where m.id = f.MPA_ID) mpa_name FROM FILMS f where id = ?";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> getFilmMapper(rs), filmId);
        if (films.isEmpty()) {
            throw new NoFilmFoundException("Film not found!");
        }

        return films.get(0);
    }

    private void validationFilm(Film film) {
        log.debug("validation film");
        try {
            if (List.of(film).stream().filter(f -> f.getReleaseDate().isBefore(minDateRelease)).findFirst().isPresent()) {
                log.error("adding film releaseDate is before " + minDateRelease.toString() + "!");
                throw new ValidationException("adding film releaseDate is before " + minDateRelease.toString() + "!");
            }
        } catch (NullPointerException e) {
            throw new ValidationException("adding film is null!");
        }
    }

    private List<Integer> checkLikes(Long filmId, Long userId) {
        log.debug("checkLikes, filmId {}, userId {}", filmId, userId);
        String sqlQuery = "SELECT \n" +
                "nvl((SELECT 1 FROM films WHERE id = ?), 0) film_exists,\n" +
                "nvl((SELECT 1 FROM users WHERE id = ?), 0) user_exists,\n" +
                "nvl((SELECT 1 FROM films_likes WHERE film_id = ? AND user_id = ?), 0) likes_exists\n" +
                "FROM dual;";
        List<List<Integer>> likesParameters = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> getLikesMapper(rs), filmId, userId, filmId, userId);
        if (likesParameters.isEmpty()) {
            throw new NoFilmFoundException("Not found!");
        }

        return likesParameters.get(0);
    }

    @Override
    public Film addLikeToFilm(Long filmId, Long userId) {
        log.debug("addLikeToFilm, filmId {}, userId {}", filmId, userId);
        List<Integer> likesParameters = checkLikes(filmId, userId);

        if (likesParameters.get(0) == 0) {
            throw new NoFilmFoundException("Film not found!");
        }

        if (likesParameters.get(1) == 0) {
            throw new NoUserFoundException("User not found!");
        }

        if (likesParameters.get(2) > 0) {
            //like уже поставлен!
            return getFilm(filmId);
        }

        String sqlQuery = "insert into films_likes(film_id, user_id) values(?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);

        return getFilm(filmId);
    }

    private List<Integer> getLikesMapper(ResultSet rs) throws SQLException {
        log.debug("getLikesMapper");
        return List.of(rs.getInt("film_exists"),
                rs.getInt("user_exists"),
                rs.getInt("likes_exists"));
    }

    public Film deleteLikeFromFilm(Long filmId, Long userId) {
        log.debug("deleteLikeFromFilm, filmId {}, userId {}", filmId, userId);
        List<Integer> likesParameters = checkLikes(filmId, userId);

        if (likesParameters.get(0) == 0) {
            throw new NoFilmFoundException("Film not found!");
        }

        if (likesParameters.get(1) == 0) {
            throw new NoUserFoundException("User not found!");
        }

        if (likesParameters.get(2) == 0) {
            //like не поставлен!
            return getFilm(filmId);
        }

        String sqlQuery = "delete from films_likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);

        return getFilm(filmId);
    }

    @Override
    public List<Film> getTopNfilms(Integer count) {
        log.debug("getTopNfilms, count {}", count);
        String sqlQuery = "select ftop.id, ftop.name, ftop.description, ftop.release_date, ftop.duration,\n" +
                "    (select string_agg(genre_id::text, ',') from genres_films gf where ftop.id = gf.film_id) genres, ftop.mpa_id,\n" +
                "    (select m.mpa_name from mpa m where m.id = ftop.mpa_id) mpa_name \n" +
                "    from films ftop,(select f.id, count(fl.film_id) cnt from films f left join films_likes fl on f.id = fl.film_id \n" +
                "    group by f.id order by 2 desc limit ? ) fgroup where ftop.id = fgroup.id ";

        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> getFilmMapper(rs), count);
        return films;
    }
}
