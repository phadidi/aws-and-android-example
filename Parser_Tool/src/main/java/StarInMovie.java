

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StarInMovie {
    private final String sname;
    private final String movieId;
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public StarInMovie(String mid, String sname) {
        this.movieId = mid;
        this.sname = sname;
    }

    public String getStarName() {
        return this.sname;
    }

    public String getMovieId() {
        return this.movieId;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("StarInMovie Details - ");
        sb.append("StarName: " + getStarName());
        sb.append(", ");
        sb.append("MovieId: " + getMovieId());
        sb.append(".");
        return sb.toString();
    }

}
