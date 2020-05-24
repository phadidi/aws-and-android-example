package edu.uci.ics.fabflixmobile;

import javax.sql.DataSource;

public class Movie {
    private final String id;
    private final String title;
    private final int year;
    private final String director;
    private final String genre;
    private final String stars;
    private DataSource dataSource;

    public Movie(String id, String movieTitle, int movieYear, String movieDirector, String genre, String stars) {
        this.id = id;
        this.title = movieTitle;
        this.year = movieYear;
        this.director = movieDirector;
        this.genre = genre;
        String names = "";
        String[] starsSplit = stars.split(",");
        for (int i = 0; i < 3; i++) {
            String[] star = starsSplit[i].split("_");
            names += star[0] + ", ";
        }
        this.stars = names.substring(0, names.length() - 2);
    }

    public String getId() {
        return this.id;
    }

    public String getGenre() {
        return this.genre;
    }

    public String getStars() {
        return this.stars;
    }

    public String getTitle() {
        return this.title;
    }

    public int getYear() {
        return this.year;
    }

    public String getDirector() {
        return this.director;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Movie Details - ");
        sb.append("Id:" + getId());
        sb.append(", ");
        sb.append("Title:" + getTitle());
        sb.append(", ");
        sb.append("Year:" + getYear());
        sb.append(", ");
        //sb.append("Genre:" + getGenre());
        //sb.append(", ");
        sb.append("Director:" + getDirector());
        sb.append(".");
        return sb.toString();
    }
}
