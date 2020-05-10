
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Genre {
    private final int id;
    private final String name;
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public Genre(int genreId, String genreName) {
        this.id = genreId;
        this.name = genreName;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void addGenreToTable() throws SQLException {
        Connection dbcon = dataSource.getConnection();
        String query = "Insert into genres VALUES(" + this.id + ", '" + this.name + "');";
        PreparedStatement statement = dbcon.prepareStatement(query);
        statement.executeUpdate();
        statement.close();
        dbcon.close();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Genre Details - ");
        sb.append("Id:" + getId());
        sb.append(", ");
        sb.append("Name:" + getName());
        sb.append(".");

        return sb.toString();
    }

}
