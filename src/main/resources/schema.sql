CREATE TABLE IF NOT EXISTS PUBLIC.FRIENDSHIP (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	FRIENDSHIP_TYPE CHARACTER VARYING(16) NOT NULL,
	CONSTRAINT FRIENDSHIP_PK PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.MPA (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	MPA_NAME CHARACTER VARYING(8) NOT NULL,
	CONSTRAINT MPA_PK PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.GENRES (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	GENRE_NAME CHARACTER VARYING(64),
	CONSTRAINT GENRES_PK PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILMS (
	ID BIGINT NOT NULL AUTO_INCREMENT,
	NAME CHARACTER VARYING(256),
	DESCRIPTION CHARACTER VARYING(1024),
	RELEASE_DATE DATE,
	DURATION INTEGER,
	MPA_ID INTEGER NOT NULL,
	CONSTRAINT FILMS_PK PRIMARY KEY (ID),
	CONSTRAINT FILMS_FK FOREIGN KEY (MPA_ID) REFERENCES PUBLIC.MPA(ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS PUBLIC.GENRES_FILMS (
	ID BIGINT NOT NULL AUTO_INCREMENT,
	FILM_ID BIGINT NOT NULL,
	GENRE_ID INTEGER NOT NULL,
	CONSTRAINT GENRES_FILMS_PK PRIMARY KEY (ID),
	CONSTRAINT GENRES_FILMS_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(ID) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT GENRES_FILMS_FK_1 FOREIGN KEY (GENRE_ID) REFERENCES PUBLIC.GENRES(ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS PUBLIC.USERS (
	ID BIGINT NOT NULL AUTO_INCREMENT,
	NAME CHARACTER VARYING(256),
	LOGIN CHARACTER VARYING(256) NOT NULL,
	EMAIL CHARACTER VARYING(256),
	BIRTHDAY DATE,
	CONSTRAINT USER_PK PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILMS_LIKES (
	ID BIGINT NOT NULL AUTO_INCREMENT,
	FILM_ID BIGINT NOT NULL,
	USER_ID BIGINT NOT NULL,
	CONSTRAINT FILMS_LIKES_PK PRIMARY KEY (ID),
	CONSTRAINT FILMS_LIKES_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(ID) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT FILMS_LIKES_FK_1 FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(ID)  ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.FRIENDS (
	ID BIGINT NOT NULL AUTO_INCREMENT,
	USER_ID BIGINT NOT NULL,
	FRIEND_ID BIGINT NOT NULL,
	FRIENDSHIP_ID INTEGER NOT NULL,
	CONSTRAINT FRIENDS_PK PRIMARY KEY (ID),
	CONSTRAINT FRIENDS_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(ID) ON DELETE CASCADE ON UPDATE CASCADE ,
	CONSTRAINT FRIENDS_FK_1 FOREIGN KEY (FRIEND_ID) REFERENCES PUBLIC.USERS(ID) ON DELETE CASCADE ON UPDATE CASCADE ,
	CONSTRAINT FRIENDS_FK_2 FOREIGN KEY (FRIENDSHIP_ID) REFERENCES PUBLIC.FRIENDSHIP(ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.REVIEWS (
    ID BIGINT NOT NULL AUTO_INCREMENT,
    FILM_ID BIGINT NOT NULL,
    USER_ID BIGINT NOT NULL,
    REVIEW CHARACTER VARYING(32678),
    IS_POSITIVE BOOLEAN NOT NULL,
    CONSTRAINT REVIEWS_PK PRIMARY KEY (ID),
    CONSTRAINT REVIEWS_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(ID) ON DELETE CASCADE ON UPDATE CASCADE ,
    CONSTRAINT REVIEWS_FK_1 FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.REVIEW_ESTIMATION (
    ID BIGINT NOT NULL AUTO_INCREMENT,
    REVIEW_ID BIGINT NOT NULL,
    USER_ID BIGINT NOT NULL,
    IS_USEFUL BOOLEAN NOT NULL,
    CONSTRAINT REVIEW_ESTIMATION_PK PRIMARY KEY (ID),
    CONSTRAINT REVIEW_ESTIMATION_FK FOREIGN KEY (REVIEW_ID) REFERENCES PUBLIC.REVIEWS(ID) ON DELETE CASCADE ON UPDATE CASCADE ,
    CONSTRAINT REVIEW_ESTIMATION_FK_1 FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.EVENT_TYPES (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	EVENT_TYPE CHARACTER VARYING(16) NOT NULL,
	CONSTRAINT EVENT_TYPES_PK PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.OPERATIONS (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	OPERATION CHARACTER VARYING(16) NOT NULL,
	CONSTRAINT OPERATIONS_PK PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.FEEDS (
    ID BIGINT NOT NULL AUTO_INCREMENT,
    CREATE_TIME TIMESTAMP NOT NULL,
    USER_ID BIGINT NOT NULL,
    EVENT_TYPE_ID INTEGER NOT NULL,
    OPERATION_ID INTEGER NOT NULL,
    ENTITY_ID BIGINT NOT NULL,
    CONSTRAINT FEED_PK PRIMARY KEY (ID),
    CONSTRAINT FEED_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(ID) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FEED_FK_1 FOREIGN KEY (EVENT_TYPE_ID) REFERENCES PUBLIC.EVENT_TYPES(ID) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FEED_FK_2 FOREIGN KEY (OPERATION_ID) REFERENCES PUBLIC.OPERATIONS(ID) ON DELETE CASCADE ON UPDATE CASCADE
);