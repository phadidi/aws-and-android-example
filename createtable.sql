SET GLOBAL sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''));

DROP DATABASE IF EXISTS `moviedb`;
CREATE DATABASE `moviedb`;
USE moviedb;

DROP TABLE IF EXISTS dbo.movies;
CREATE TABLE `movies` (
  `id` varchar(10) NOT NULL,
  `title` varchar(100) NOT NULL,
  `year` int NOT NULL,
  `director` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

DROP TABLE IF EXISTS dbo.stars;
CREATE TABLE `stars` (
  `id` varchar(10) NOT NULL,
  `name` varchar(100) NOT NULL,
  `birthYear` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

DROP TABLE IF EXISTS dbo.stars_in_movies;
CREATE TABLE `stars_in_movies` (
  `starId` varchar(10) NOT NULL,
  `movieId` varchar(10) NOT NULL,
  KEY `_idx` (`starId`),
  KEY `movieId_idx` (`movieId`),
  CONSTRAINT `movieId` FOREIGN KEY (`movieId`) REFERENCES `movies` (`id`),
  CONSTRAINT `starId` FOREIGN KEY (`starId`) REFERENCES `stars` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

DROP TABLE IF EXISTS dbo.genres;
CREATE TABLE `genres` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

DROP TABLE IF EXISTS dbo.genres_in_movies;
CREATE TABLE `genres_in_movies` (
  `genreId` int NOT NULL,
  `movieId` varchar(10) NOT NULL,
  KEY `genreId_idx` (`genreId`),
  KEY `movieId_idx` (`movieId`),
  CONSTRAINT `genreIdRef` FOREIGN KEY (`genreId`) REFERENCES `genres` (`id`),
  CONSTRAINT `movieIdRef` FOREIGN KEY (`movieId`) REFERENCES `movies` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

DROP TABLE IF EXISTS dbo.creditcards;
CREATE TABLE `creditcards` (
  `id` varchar(20) NOT NULL,
  `firstName` varchar(50) NOT NULL,
  `lastName` varchar(50) NOT NULL,
  `expiration` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

DROP TABLE IF EXISTS dbo.customers;
CREATE TABLE `customers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `firstName` varchar(50) NOT NULL,
  `lastName` varchar(50) NOT NULL,
  `ccId` varchar(20) NOT NULL,
  `address` varchar(200) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `creditcardId_idx` (`ccId`),
  CONSTRAINT `creditcardId` FOREIGN KEY (`ccId`) REFERENCES `creditcards` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

DROP TABLE IF EXISTS dbo.sales;
CREATE TABLE `sales` (
  `idsales` int NOT NULL AUTO_INCREMENT,
  `customerId` int NOT NULL,
  `movieId` varchar(10) NOT NULL,
  `saleDate` date NOT NULL,
  PRIMARY KEY (`idsales`),
  KEY `customerIdRef_idx` (`customerId`),
  KEY `movieIdRef_idx` (`movieId`),
  KEY `customerIdSale_idx` (`customerId`),
  KEY `movieIdSale_idx` (`movieId`),
  CONSTRAINT `customerIdSale` FOREIGN KEY (`customerId`) REFERENCES `customers` (`id`),
  CONSTRAINT `movieIdSale` FOREIGN KEY (`movieId`) REFERENCES `movies` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

DROP TABLE IF EXISTS dbo.ratings;
CREATE TABLE `ratings` (
  `movieId` varchar(10) NOT NULL,
  `rating` float NOT NULL,
  `numVotes` int NOT NULL,
  PRIMARY KEY (`movieId`),
  CONSTRAINT `movieIdRating` FOREIGN KEY (`movieId`) REFERENCES `movies` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;