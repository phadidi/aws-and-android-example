

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GenreInMovie {
    private final String genreId;
    private final String movieId;
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public GenreInMovie(String gid, String mid) {
        this.genreId = gid;
        this.movieId = mid;
    }

    public String getGenreId() {
        return this.genreId;
    }

    public String getMovieId() {
        return this.movieId;
    }

    public void addGenreInMovieToTable() throws SQLException {
        Connection dbcon = dataSource.getConnection();
        String query = "Insert into genres_in_movies VALUES('" + this.genreId + "', '" + this.movieId + "');";
        PreparedStatement statement = dbcon.prepareStatement(query);
        statement.executeUpdate();
        statement.close();
        dbcon.close();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("GenreInMovie Details - ");
        sb.append("GenreId:" + getGenreId());
        sb.append(", ");
        sb.append("MovieId:" + getMovieId());
        sb.append(".");
        return sb.toString();
    }

}
