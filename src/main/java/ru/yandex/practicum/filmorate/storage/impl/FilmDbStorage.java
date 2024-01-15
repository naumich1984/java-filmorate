package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final LocalDate minDateRelease = LocalDate.parse("1895-12-28");
    private final int countTopFilm = 10;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getAllFilms() {
        log.debug("getAllFilms");
        String sqlQuery = "select " +
                "   f.id, " +
                "   f.name, " +
                "   f.description, " +
                "   f.release_date, " +
                "   f.duration, " +
                "   string_agg(gf.genre_id::text, ',') as genres, " +
                "   string_agg(df.director_id::text, ',') as directors, " +
                "   f.mpa_id, " +
                "   m.mpa_name " +
                "from films f " +
                "left join genres_films gf on f.id = gf.film_id " +
                "left join directors_films df on f.id = df.film_id " +
                "inner join mpa m on m.id = f.mpa_id " +
                "group by " +
                "   f.id, " +
                "   f.name, " +
                "   f.description, " +
                "   f.release_date, " +
                "   f.duration, " +
                "   f.mpa_id, " +
                "   m.mpa_name ";
        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> getFilmMapper(rs), null);
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
                    .map(f -> getAllGenres().stream().filter(g -> g.getId().equals(Integer.parseInt(f)))
                            .findFirst().get())
                    .collect(Collectors.toList());
        } else {
            genresList = Collections.emptyList();
        }

        genresList.sort((o1, o2) -> o1.getId() - o2.getId());

        List<Director> directorList;
        if (rs.getString("directors") != null) {
            directorList = Arrays.stream(Optional.ofNullable(rs.getString("directors")).orElse("").split(","))
                .map(f -> getAllDirectors().stream().filter(g -> g.getId().equals(Integer.parseInt(f)))
                    .findFirst().get())
                .collect(Collectors.toList());
        } else {
            directorList = Collections.emptyList();
        }

        directorList.sort((o1, o2) -> o1.getId() - o2.getId());

        return new Film(rs.getLong("id"), rs.getString("name"),
                rs.getString("description"), LocalDate.parse(rs.getString("release_date")),
                rs.getInt("duration"), genresList, directorList, new Mpa(rs.getInt("mpa_id"),
                rs.getString("mpa_name")));
    }

    @Override
    public Film addFilm(Film film) {
        log.debug("addFilm, filmId {}", film.getId());
        existsFilm(film.getId(), "adding film already exists!", 1);
        validateFilm(film);
        String sqlQuery = "insert into films(name, description, release_date, duration, mpa_id) "
                + "values (?, ?, ?, ?, ?)";
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
            Set<Genre> uniqueGenres = new HashSet<>(film.getGenres());
            List<Genre> genresList = new ArrayList<>();
            genresList.addAll(uniqueGenres);
            genresList.sort((o1, o2) -> o2.getId() - o1.getId());
            for (Genre genre : uniqueGenres) {
                String sqlQueryGenres = "insert into genres_films(film_id, genre_id) " + "values (?, ?)";
                jdbcTemplate.update(sqlQueryGenres, filmId, genre.getId());
            }
        }

        if (film.getDirectors() != null) {
            Long filmId = film.getId();
            Set<Director> uniqueDirectors = new HashSet<>(film.getDirectors());
            List<Director> directorList = new ArrayList<>();
            directorList.addAll(uniqueDirectors);
            directorList.sort((o1, o2) -> o2.getId() - o1.getId());
            for (Director director : uniqueDirectors) {
                String sqlQueryDirector = "insert into directors_films(film_id, director_id) " + "values (?, ?)";
                jdbcTemplate.update(sqlQueryDirector, filmId, director.getId());
            }
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("updateFilm, filmId {}", film.getId());
        existsFilm(film.getId(), "updating film not exists!", 0);
        validateFilm(film);
        String sqlQuery = "update films set " + "name = ?, description = ?, release_date = ? , duration = ?, mpa_id = ?"
                + "where id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());

        sqlQuery = "delete from genres_films where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());

        List<Genre> genresList = new ArrayList<>();
        if (film.getGenres() != null) {
            Long filmId = film.getId();
            Set<Genre> uniqueGenres = new HashSet<Genre>(film.getGenres());
            genresList.addAll(uniqueGenres);
            for (Genre genre : uniqueGenres) {
                String sqlQueryGenres = "insert into genres_films(film_id, genre_id) " + "values (?, ?)";
                jdbcTemplate.update(sqlQueryGenres, filmId, genre.getId());
            }
        }
        film.setGenres(genresList);

        sqlQuery = "delete from directors_films where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());

        List<Director> directorList = new ArrayList<>();
        if (film.getDirectors() != null) {
            Long filmId = film.getId();
            Set<Director> uniqueDirector = new HashSet<>(film.getDirectors());
            directorList.addAll(uniqueDirector);
            for (Director director : uniqueDirector) {
                String sqlQueryDirector = "insert into directors_films(film_id, director_id) " +
                                            "values (?, ?)";
                jdbcTemplate.update(sqlQueryDirector, filmId, director.getId());
            }
        }
        film.setDirectors(directorList);

        return film;
    }

    @Override
    public List<Genre> getAllGenres() {
        log.debug("getAllGenres");
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
    public List<Mpa> getAllMpa() {
        log.debug("getAllMpa");
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
            throw new NotFoundException(errorMessage);
        }
    }

    private Integer existsFilmMapper(ResultSet rs) throws SQLException {
        log.debug("existsFilmMapper");

        return rs.getInt(1);
    }

    @Override
    public Film getFilm(Long filmId) {
        log.debug("getFilm, filmId {}", filmId);
        String sqlQuery = "select " +
                "   f.id, " +
                "   f.name, " +
                "   f.description, " +
                "   f.release_date, " +
                "   f.duration, " +
                "   string_agg(gf.genre_id::text, ',') as genres, " +
                "   string_agg(df.director_id::text, ',') as directors, " +
                "   f.mpa_id, " +
                "   m.mpa_name " +
                "from films f " +
                "left join genres_films gf on f.id = gf.film_id " +
                "left join directors_films df on f.id = df.film_id " +
                "inner join mpa m on m.id = f.mpa_id where f.id = ? " +
                "group by " +
                "   f.id, " +
                "   f.name, " +
                "   f.description, " +
                "   f.release_date, " +
                "   f.duration, " +
                "   f.mpa_id, " +
                "   m.mpa_name ";
        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> getFilmMapper(rs), filmId);
        if (films.isEmpty()) {
            throw new NotFoundException("Film not found!");
        }

        return films.get(0);
    }

    private void validateFilm(Film film) {
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
        String sqlQuery = "SELECT \n" + "nvl((SELECT 1 FROM films WHERE id = ?), 0) film_exists,\n"
                + "nvl((SELECT 1 FROM users WHERE id = ?), 0) user_exists,\n"
                + "nvl((SELECT 1 FROM films_likes WHERE film_id = ? AND user_id = ?), 0) likes_exists\n"
                + "FROM dual;";
        List<List<Integer>> likesParameters = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> getLikesMapper(rs),
                filmId, userId, filmId, userId);
        if (likesParameters.isEmpty()) {
            throw new NotFoundException("Not found!");
        }

        return likesParameters.get(0);
    }

    @Override
    public Film addLikeToFilm(Long filmId, Long userId) {
        log.debug("addLikeToFilm, filmId {}, userId {}", filmId, userId);
        List<Integer> likesParameters = checkLikes(filmId, userId);

        if (likesParameters.get(0) == 0) {
            throw new NotFoundException("Film not found!");
        }

        if (likesParameters.get(1) == 0) {
            throw new NotFoundException("User not found!");
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

        return List.of(rs.getInt("film_exists"), rs.getInt("user_exists"), rs.getInt("likes_exists"));
    }

    public Film deleteLikeFromFilm(Long filmId, Long userId) {
        log.debug("deleteLikeFromFilm, filmId {}, userId {}", filmId, userId);
        List<Integer> likesParameters = checkLikes(filmId, userId);

        if (likesParameters.get(0) == 0) {
            throw new NotFoundException("Film not found!");
        }

        if (likesParameters.get(1) == 0) {
            throw new NotFoundException("User not found!");
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
    public List<Film> getTopNfilms(Integer count, Integer genreId, Integer year) {
        int countTop = count != null ? count : countTopFilm;
        log.debug("getTopNfilms, count {}", countTop);
        StringBuilder sqlQuery = new StringBuilder("select\n" +
                "   ff.id, \n" +
                "   ff.name, \n" +
                "   ff.description, \n" +
                "   ff.release_date, \n" +
                "   ff.duration,\n" +
                "   ff.genres, \n" +
                "   ff.directors, \n" +
                "   ff.mpa_id,\n" +
                "   ff.mpa_name,\n" +
                "   count(fl.film_id) as cnt_likes\n" +
                "from\n" +
                "( select f.id, \n" +
                "    f.name, \n" +
                "    f.description, \n" +
                "    f.release_date, \n" +
                "    f.duration,\n" +
                "    string_agg(distinct gf.genre_id::text, ',') as genres, \n" +
                "    string_agg(distinct df.director_id::text, ',') as directors, \n" +
                "    f.mpa_id,\n" +
                "    m.mpa_name\n" +
                "    from films f " +
                "left join genres_films gf \n" +
                "    on f.id = gf.film_id " +
                "left join directors_films df on f.id = df.film_id " +
                "inner join mpa m on m.id = f.mpa_id\n " +
                " where 1 = 1 ");
                if (Optional.ofNullable(year).isPresent()) {
                    sqlQuery.append(" and YEAR(f.RELEASE_DATE)  = ").append(year).append(" ");
                }
                sqlQuery.append(
                "  group by     f.id,\n" +
                "               f.name, \n" +
                "               f.description, \n" +
                "               f.release_date,\n" +
                "               f.duration, \n" +
                "               f.mpa_id,\n" +
                "               m.mpa_name\n" +
                ") ff left join films_likes fl on ff.id = fl.film_id\n ");
                if (Optional.ofNullable(genreId).isPresent()) {
                    sqlQuery.append("INNER JOIN genres_films gf2 ON ff.ID  = gf2.FILM_ID AND gf2.GENRE_ID = ").append(genreId);
                }
                sqlQuery.append(
                " group by ff.id, \n" +
                "     ff.name, \n" +
                "     ff.description, \n" +
                "     ff.release_date, \n" +
                "     ff.duration,\n" +
                "     ff.genres, \n" +
                "     ff.mpa_id,\n" +
                "     ff.mpa_name\n" +
                "order by cnt_likes desc\n" +
                "limit ? ");

        List<Film> films = jdbcTemplate.query(sqlQuery.toString(), (rs, rowNum) -> getFilmMapper(rs), countTop);

        return films;
    }

    @Override
    public Integer deleteFilm(Long filmId) {
        String sql = "DELETE FROM films WHERE id=?;";
        return jdbcTemplate.update(sql, filmId);
    }

    @Override
    public List<Director> getAllDirectors() {
        log.debug("getAllDirectors");
        String sql = "select director_id, director_name from directors";
        List<Director> directors = jdbcTemplate.query(sql, getDirectorMapper());
        if (directors.isEmpty()) {
            return Collections.emptyList();
        }

        return directors;
    }

    @Override
    public Director getDirector(Integer id) {
        log.debug("getDirector");
        try {
            String sql = "SELECT director_id, director_name FROM directors WHERE director_id = ?;";
            Director director = jdbcTemplate.queryForObject(sql, getDirectorMapper(), id);

            log.info("Founded director with id = {}", id);
            return director;
        } catch (RuntimeException e) {
            log.warn("Not found director with id = {}", id);
            throw new NotFoundException("Not found director with id = " + id);
        }
    }

    public List<Director> getDirectorsByFilmId(Long filmId) {
        log.debug("getDirectorsByFilmId");
        if (getFilm(filmId) == null) {
            log.warn("Not found film with id = {}", filmId);
            throw new NotFoundException("Not found film with id = " + filmId);
        }

        String sql = "SELECT "
            + "d.director_id, "
            + "d.director_name "
            + "from directors d"
            + "join directors_films df on d.id = df.director_id"
            + "where df.film_id = ?;";

        List<Director> directors = jdbcTemplate.query(sql, getDirectorMapper());
        if (directors.isEmpty()) {
            return Collections.emptyList();
        }

        return directors;
    }

    @Override
    public List<Film> getDirectorsFilmSortBy(Integer directorId, String sort) {
        log.debug("getDirectorsFilmSortBy");
        if (!sort.equals("year") && !sort.equals("likes")) {
            throw new NotFoundException("Bad sortBy request");
        }

        if (getDirector(directorId) == null) {
            log.warn("Not found director with id = {}", directorId);
            throw new NotFoundException("Not found director with id = " + directorId);
        }

        String sqlByLikes = "SELECT "
            + "f.id, "
            + "f.name, "
            + "f.description, "
            + "f.release_date, "
            + "f.duration, "
            + "string_agg(gf.genre_id::text, ',') as genres, "
            + "string_agg(df.director_id::text, ',') as directors, "
            + "f.mpa_id, "
            + "m.mpa_name "
            + "FROM films f "
            + "LEFT JOIN genres_films gf ON f.id = gf.film_id "
            + "LEFT JOIN films_likes l ON f.id = l.film_id "
            + "JOIN directors_films df ON f.id = df.film_id "
            + "INNER JOIN mpa m ON m.id = f.mpa_id "
            + "WHERE df.director_id = ? "
            + "GROUP BY f.id, "
            + "f.name, "
            + "f.description, "
            + "f.release_date, "
            + "f.duration, "
            + "f.mpa_id, "
            + "m.mpa_name "
            + "ORDER BY count(l.film_id) DESC;";

        String sqlByYear = "SELECT "
            + "f.id, "
            + "f.name, "
            + "f.description, "
            + "f.release_date, "
            + "f.duration, "
            + "string_agg(gf.genre_id::text, ',') as genres, "
            + "string_agg(df.director_id::text, ',') as directors, "
            + "f.mpa_id, "
            + "m.mpa_name "
            + "FROM films f "
            + "LEFT JOIN genres_films gf ON f.id = gf.film_id "
            + "JOIN directors_films df ON f.id = df.film_id "
            + "JOIN directors d ON d.director_id = df.director_id "
            + "INNER JOIN mpa m ON m.id = f.mpa_id "
            + "WHERE d.director_id = ? "
            + "GROUP BY f.id, "
            + "f.name, "
            + "f.description, "
            + "f.release_date, "
            + "f.duration, "
            + "f.mpa_id, "
            + "m.mpa_name "
            + "ORDER BY EXTRACT(YEAR FROM f.release_date);";

        List<Film> films;

        switch (sort) {
            case "likes":
                films = jdbcTemplate.query(sqlByLikes, (rs, rowNum) -> getFilmMapper(rs), directorId);
                break;
            case "year":
                films = jdbcTemplate.query(sqlByYear, (rs, rowNum) -> getFilmMapper(rs), directorId);
                break;
            default:
                films = new ArrayList<>();
        }

        return films;
    }

    @Override
    public Director createDirector(Director director) {
        log.debug("createDirector");

        if (director.getName().isEmpty() || director.getName().isBlank()) {
            throw new ValidationException("Name must be not blank");
        }

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("directors")
            .usingGeneratedKeyColumns("director_id");

        int id = insert.executeAndReturnKey(directorToMap(director)).intValue();
        director.setId(id);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        log.debug("updateDirector");
        if (getDirector(director.getId()) == null) {
            log.warn("Not found director with id = {}", director.getId());
            throw new NotFoundException("Not found director with id = " + director.getId());
        }

        String sql = "UPDATE directors SET "
            + "director_name = ?"
            + " WHERE director_id = ?;";

        jdbcTemplate.update(sql, director.getName(), director.getId());

        log.info("Updated director with id = {}", director.getId());
        return getDirector(director.getId());
    }

    @Override
    public Integer deleteDirector(Integer id) {
        log.debug("deleteDirector");
        String sql = "DELETE FROM directors WHERE director_id = ?;";
        return jdbcTemplate.update(sql, id);
    }

    private static RowMapper<Director> getDirectorMapper() {
        log.debug("getDirectorMapper");
        return ((rs, rowNum) -> Director.builder()
            .id(rs.getInt("director_id"))
            .name(rs.getString("director_name"))
            .build());
    }

    private static Map<String, Object> directorToMap(Director director) {
        return Map.of(
            "director_name", director.getName()
        );
    }

    private static RowMapper<Long> filmIdMapper() {
        log.debug("filmIdMapper");
        return ((rs, rowNum) -> rs.getLong("id"));
    }

    @Override
    public List<Film> getFilmByQuery(String query, String by) {
        log.debug("getFilmByQuery");
        StringBuilder sql = new StringBuilder("select " +
                "   f.id, " +
                "   f.name, " +
                "   f.description, " +
                "   f.release_date, " +
                "   f.duration, " +
                "   string_agg(distinct gf.genre_id::text, ',') as genres, " +
                "   string_agg(distinct df.director_id::text, ',') as directors, " +
                "   f.mpa_id, " +
                "   m.mpa_name " +
                " from films f " +
                " left join films_likes fl on f.id = fl.film_id " +
                " left join genres_films gf on f.id = gf.film_id " +
                " left join mpa m on m.id = f.mpa_id " +
                " left join directors_films df on f.id = df.film_id " +
                " left join directors d on df.director_id = d.director_id ");
        if (by.equals("title")) {
            sql.append(" where lower(f.name) like lower('%").append(query).append("%') ");
        } else if (by.equals("director")) {
            sql.append(" where lower(d.director_name) like lower('%").append(query).append("%') ");
        } else if (by.equals("title,director") || by.equals("director,title")) {
            sql.append(" where lower(f.name) like lower('%").append(query).append("%') ");
            sql.append(" or lower(d.director_name) like lower('%").append(query).append("%') ");
        }
        sql.append(" group by f.id order by count(fl.film_id) desc");

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> getFilmMapper(rs));
    }

    @Override
    public List<Film> getCommonFilms(long userId, Long friendId) {
        String sqlQuery = "select " +
                "   f.id, " +
                "   f.name, " +
                "   f.description, " +
                "   f.release_date, " +
                "   f.duration, " +
                "   string_agg(gf.genre_id::text, ',') as genres, " +
                "   string_agg(df.director_id::text, ',') as directors, " +
                "   f.mpa_id, " +
                "   m.mpa_name " +
                "from films f " +
                "left join genres_films gf on f.id = gf.film_id " +
                "left join directors_films df on f.id = df.film_id " +
                "inner join mpa m on m.id = f.mpa_id  " +
                "inner join ( " +
                " SELECT fl.FILM_ID  FROM FILMS_LIKES fl WHERE fl.USER_ID = ? " +
                " INTERSECT   " +
                " SELECT fl.FILM_ID  FROM FILMS_LIKES fl WHERE fl.USER_ID = ?  " +
                ") fi on f.id  = fi.film_id " +
                " LEFT JOIN FILMS_LIKES fl2 ON f.id = fl2.film_id " +
                "group by " +
                "   f.id, " +
                "   f.name, " +
                "   f.description, " +
                "   f.release_date, " +
                "   f.duration, " +
                "   f.mpa_id, " +
                "   m.mpa_name " +
                " ORDER BY count(fl2.film_id) desc ";
        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> getFilmMapper(rs), userId, friendId);
        if (films.isEmpty()) {
            return Collections.emptyList();
        }

        return films;
    }
}
