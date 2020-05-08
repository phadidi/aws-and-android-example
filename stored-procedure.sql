USE `moviedb`;
DROP procedure IF EXISTS `add_movie`;

DELIMITER $$
USE `moviedb`$$
CREATE
    DEFINER = `mytestuser`@`localhost` PROCEDURE `add_movie`(movieTitle VARCHAR(100), movieYear INT,
                                                             movieDirector VARCHAR(100), movieStar VARCHAR(100),
                                                             movieGenre VARCHAR(32))
BEGIN
    INSERT INTO movies
    VALUES (CONCAT('tt', (select LPAD(substring((select max(id) from movies), 3) + 1, 7, '0'))), movieTitle, movieYear,
            movieDirector);
    INSERT INTO stars_in_movies VALUES ((SELECT id from stars where name = movieStar LIMIT 1), movieId);
    INSERT INTO genres_in_movies VALUES ((SELECT id from genres where name = movieGenre LIMIT 1), movieId);
END$$

DELIMITER ;