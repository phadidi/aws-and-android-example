import javax.annotation.Resource;
import javax.sql.DataSource;

public class Star {
    //private final String id;
    private final String name;
    private final int birthYear;
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public Star(String starName, int starBirthYear) {
        //this.id = starId;
        this.name = starName;
        this.birthYear = starBirthYear;
    }

//    public String getId() {
//        return this.id;
//    }

    public String getName() {
        return this.name;
    }

    public int getBirthYear() {
        return this.birthYear;
    }

//    public void addStarToTable() throws SQLException {
//        Connection dbcon = dataSource.getConnection();
//        String query = "Insert into stars VALUES('" + this.id + "', '" + this.name + "', " + this.birthYear + ");";
//        PreparedStatement statement = dbcon.prepareStatement(query);
//        statement.executeUpdate();
//        statement.close();
//        dbcon.close();
//    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Star Details - ");
        //sb.append("Id:" + getId());
        //sb.append(", ");
        sb.append("Name:" + getName());
        sb.append(", ");
        sb.append("BirthYear:" + getBirthYear());
        sb.append(".");
        return sb.toString();
    }

}
