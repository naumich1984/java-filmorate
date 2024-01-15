MERGE INTO MPA (ID, MPA_NAME)
VALUES (1, 'G')
    ,(2, 'PG')
    ,(3, 'PG-13')
    ,(4, 'R')
    ,(5, 'NC-17');

MERGE INTO GENRES (ID, GENRE_NAME)
VALUES (1, 'Комедия')
    ,(2, 'Драма')
    ,(3, 'Мультфильм')
    ,(4, 'Триллер')
    ,(5, 'Документальный')
    ,(6, 'Боевик');

MERGE INTO FRIENDSHIP (ID, FRIENDSHIP_TYPE) VALUES (1, 'подтверждённая'), (2, 'неподтверждённая');

MERGE INTO EVENT_TYPES (ID, EVENT_TYPE) VALUES (1, 'LIKE'), (2, 'REVIEW'), (3, 'FRIEND');

MERGE INTO OPERATIONS (ID, OPERATION) VALUES (1, 'REMOVE'), (2, 'ADD'), (3, 'UPDATE');