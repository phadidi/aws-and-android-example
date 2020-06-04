package main.java;

//import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Employee {
    private final String email;
//    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    private String password;
    private String fullName;

    public Employee(String emailAddress, String pw, String name) {
        this.email = emailAddress;
        this.password = pw;
        this.fullName = name;
    }

//    public void addEmployeeToTable() throws SQLException {
//        Connection dbcon = dataSource.getConnection();
//        String query = "Insert into employees VALUES(" + this.email + ", '" + this.password + "', '" + this.fullName + "');";
//        PreparedStatement statement = dbcon.prepareStatement(query);
//        statement.executeUpdate();
//        statement.close();
//        dbcon.close();
//    }
}
