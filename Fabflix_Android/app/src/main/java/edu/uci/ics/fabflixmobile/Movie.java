package edu.uci.ics.fabflixmobile;

import javax.sql.DataSource;
import java.util.ArrayList;

public class Movie {
    private final String id;
    //private String db_id = "";
    private final String title;
    private final int year;
    private final String director;
    private final String genre;
    private final String stars;
    //@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public Movie(String id, String movieTitle, int movieYear, String movieDirector, String genre, String stars) {
//        String idQuery = "SELECT CONCAT('tt', LPAD(substring((select max(id) from movies), 3) + 1, 7, '0')) as movieId;";
//        Connection dbcon = dataSource.getConnection();
//        PreparedStatement statementId = dbcon.prepareStatement(idQuery);
//        ResultSet rs = statementId.executeQuery();
//        boolean idInit = false; // check if rs was empty
//        String tempId = "";
//        while (rs.next()) {
//            tempId = rs.getString("movieId");
//            idInit = true;
//            break; // stop after one loop due to final variable
//        }

        //this.id = tempId;
        this.id = id;
        this.title = movieTitle;
        this.year = movieYear;
        this.director = movieDirector;
        this.genre = genre;
        String names = "";
        String[] starsSplit = stars.split(",");
        int size = starsSplit.length;
        if (size > 3) {
            size = 3;
        }
        for (int i = 0; i < size; i++) {
            String[] star = starsSplit[i].split("_");
            names += star[0] + ", ";
        }
        this.stars = names.substring(0, names.length() - 2);
        //rs.close();
        //statementId.close();
        //dbcon.close();
    }

//    public void setDbId(String id){
//        this.db_id = id;
//    }
//
//    public String getDbId(){
//        return this.db_id;
//    }

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

//    public void addMovieToTable() throws SQLException {
//        Connection dbcon = dataSource.getConnection();
//        String query = "Insert into movies VALUES('" + this.id + "', '" + this.title + "', " + this.year
//                + ", '" + this.director + "');";
//        PreparedStatement statement = dbcon.prepareStatement(query);
//        statement.executeUpdate();
//        statement.close();
//        dbcon.close();
//    }

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
