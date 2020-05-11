USE `moviedb`;
DROP procedure IF EXISTS `add_movie`;

DELIMITER $$
USE `moviedb`$$
CREATE
    DEFINER = `mytestuser`@`localhost` PROCEDURE `add_movie`(movieTitle VARCHAR(100), movieYear INT,
                                                             movieDirector VARCHAR(100), movieStar VARCHAR(100),
                                                             movieGenre VARCHAR(32))
BEGIN
    IF ((SELECT COUNT(*) FROM stars WHERE name = movieStar) = 0) THEN
		SET @starId = CONCAT('nm', (select LPAD(substring((select max(id) from stars), 3) + 1, 7, '0')));
        INSERT INTO stars VALUES (@starId, movieStar, NULL);
    END IF;
    IF ((SELECT COUNT(*) FROM genres WHERE name = movieGenre) = 0) THEN
		SET @genreId = (select max(id) + 1 from genres);
        INSERT INTO genres VALUES (@genreId, movieGenre);
    END IF;
    SET @movieId = (SELECT CONCAT('tt', LPAD(substring((select max(id) from movies), 3) + 1, 7, '0')));
    INSERT INTO movies VALUES (@movieId, movieTitle, movieYear, movieDirector);
    INSERT INTO stars_in_movies VALUES ((SELECT id from stars where name = movieStar LIMIT 1), @movieId);
    INSERT INTO genres_in_movies VALUES ((SELECT id from genres where name = movieGenre LIMIT 1), @movieId);
END$$

DELIMITER ;

call add_movie('Pineapple Pool', 2020, 'Alex Valadez', 'Neema Atashbar', 'Drama');
