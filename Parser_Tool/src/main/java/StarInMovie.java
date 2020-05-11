import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StarInMovie {
    private final String starId;
    private final String movieId;
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public StarInMovie(String sid, String mid) {
        this.starId = sid;
        this.movieId = mid;
    }

    public String getStarId() {
        return this.starId;
    }

    public String getMovieId() {
        return this.movieId;
    }

    public void addStarInMovieToTable() throws SQLException {
        Connection dbcon = dataSource.getConnection();
        String query = "Insert into stars_in_movies VALUES('" + this.starId + "', '" + this.movieId + "');";
        PreparedStatement statement = dbcon.prepareStatement(query);
        statement.executeUpdate();
        statement.close();
        dbcon.close();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("StarInMovie Details - ");
        sb.append("StarId:" + getStarId());
        sb.append(", ");
        sb.append("MovieId:" + getMovieId());
        sb.append(".");
        return sb.toString();
    }

}
