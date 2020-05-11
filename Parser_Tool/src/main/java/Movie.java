

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Movie {
    private final String id;
    //private String db_id = "";
    private final String title;
    private final int year;
    private final String director;
    private final String genre;
    //@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public Movie(String id, String movieTitle, int movieYear, String movieDirector, String genre) {
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

    public String getGenre(){
        return this.genre;
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
        sb.append("Genre:" + getGenre());
        sb.append(", ");
        sb.append("Director:" + getDirector());
        sb.append(".");

        return sb.toString();
    }
}
