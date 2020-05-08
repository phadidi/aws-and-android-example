package main.java;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class Movie {
    private final String id;
    private final String title;
    private final int year;
    private final String director;
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public Movie(String movieId, String movieTitle, int movieYear, String movieDirector) {
        this.id = movieId;
        this.title = movieTitle;
        this.year = movieYear;
        this.director = movieDirector;
    }

    public String getId() {
        return this.id;
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

    public void addMovieToTable() throws SQLException {
        Connection dbcon = dataSource.getConnection();
        String query = "Insert into movies VALUES('" + this.id + "', '" + this.title + "', " + this.year
                + ", '" + this.director + "');";
        PreparedStatement statement = dbcon.prepareStatement(query);
        statement.executeUpdate();
        statement.close();
        dbcon.close();
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
        sb.append("Director:" + getDirector());
        sb.append(".");

        return sb.toString();
    }
}
