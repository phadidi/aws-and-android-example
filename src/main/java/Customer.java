package main.java;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Customer {

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    private int id;
    private final String email;
    private String password;
    private String firstName;
    private String lastName;
    private String creditCardId;
    private String address;

    public Customer (String address) {
        this.email = address;
    }

    public Customer (int customerId, String emailAddress, String pw, String first, String last, String ccId, String billingAddress) {
        this.id = customerId;
        this.email = emailAddress;
        this.password = pw;
        this.firstName = first;
        this.lastName = last;
        this.creditCardId = ccId;
        this.address = billingAddress;
    }

    public void addCustomerToTable(Customer c) throws SQLException {
        Connection dbcon = dataSource.getConnection();
        Statement statement = dbcon.createStatement();
        String query = "Insert into customers VALUES(" + Integer.toString(c.id) + ", '" + c.firstName + "', '" + c.lastName
                + "', '" + c.creditCardId + "', '" + c.address + "', '" + c.email + "', '" + c.password + "');";
        statement.executeUpdate(query);
        statement.close();
        dbcon.close();
    }

}
