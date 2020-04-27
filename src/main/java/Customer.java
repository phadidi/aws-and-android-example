package main.java;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Customer {

    private final String email;
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    private int id;
    private String password;
    private String firstName;
    private String lastName;
    private String creditCardId;
    private String address;
    private Map<String, Integer> cart;

    public Customer(int customerId, String emailAddress, String pw, String first, String last, String ccId, String billingAddress) {
        this.id = customerId;
        this.email = emailAddress;
        this.password = pw;
        this.firstName = first;
        this.lastName = last;
        this.creditCardId = ccId;
        this.address = billingAddress;
        this.cart = new HashMap<String, Integer>();
    }

    public void addCustomerToTable() throws SQLException {
        Connection dbcon = dataSource.getConnection();
        Statement statement = dbcon.createStatement();
        String query = "Insert into customers VALUES(" + Integer.toString(this.id) + ", '" + this.firstName + "', '" + this.lastName
                + "', '" + this.creditCardId + "', '" + this.address + "', '" + this.email + "', '" + this.password + "');";
        statement.executeUpdate(query);
        statement.close();
        dbcon.close();
    }

    public Map<String, Integer> getCart() {
        return cart;
    }

    public void addToCart(String movieId) {
        Integer quantity = cart.get(movieId);
        cart.put(movieId, (quantity == null) ? 1 : quantity + 1);
    }

    public void removeFromCart(String movieId) {
        if (cart.containsKey(movieId))
            cart.remove(movieId);
    }

    public void checkoutCart() {
        cart.clear();
    }

}
