USE `moviedb`;
DROP procedure IF EXISTS `add_movie`;

DELIMITER $$
USE `moviedb`$$
CREATE PROCEDURE `add_movie`(movieId VARCHAR(10) , movieTitle VARCHAR(100), movieYear INT, movieDirector VARCHAR(100), movieStar VARCHAR(100), movieGenre VARCHAR(32))
BEGIN
    INSERT INTO movies VALUES(movieId, movieTitle, movieYear, movieDirector);
    INSERT INTO stars_in_movies VALUES((SELECT id from stars where name = movieStar LIMIT 1), movieId);
    INSERT INTO genres_in_movies VALUES((SELECT id from genres where name = movieGenre LIMIT 1), movieId);
END$$

DELIMITER ;