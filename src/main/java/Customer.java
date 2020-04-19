package main.java;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;


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
    private List<String> cart;

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
        this.cart = new ArrayList<String>();
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

    public void addToCart(String movieId) {
        cart.add(movieId);
    }

    public void removeFromCart(String movieId) {
        if (cart.contains(movieId))
            cart.remove(movieId);
    }

    public void checkoutCart() {
        cart.clear();
    }

}
