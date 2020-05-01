package main.java;

import com.google.gson.JsonObject;
import com.mysql.jdbc.PreparedStatement;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            //Statement statement = dbcon.createStatement();

        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
            String resultEmail = "";
            String resultPassword = "";
            int resultId = (int) (Math.random() * 1000000);
            String resultFirstname = "";
            String resultLastname = "";
            String resultCreditCard = "";
            String resultAddress = "";

            //TODO: modify code passing for resultId for preparedStatement so casting isn't needed!
            //String queryUniqueResult = "select id from customers where id = '" + Integer.toString(resultId) + "';";
            PreparedStatement statement = (PreparedStatement) dbcon.prepareStatement("select id from customers where id = '?';");
            statement.setString(1, Integer.toString(resultId));
            ResultSet rsUniqueResult = statement.executeQuery();
            while (rsUniqueResult.next()) {
                int tempId = rsUniqueResult.getInt("id");
                while (tempId == resultId) { // check if customer with id already exists
                    resultId = (int) (Math.random() * 1000000);
                    statement.setString(1, Integer.toString(resultId));
                    rsUniqueResult = statement.executeQuery();
                }
            }

            PreparedStatement statementLogin = (PreparedStatement) dbcon.prepareStatement("select * from customers where email = '?' and password = '?';");
            statementLogin.setString(1, email);
            statementLogin.setString(2, password);
            //String query = "select * from customers where email = '" + email + "' and password = '" + password + "';";
            ResultSet rs = statementLogin.executeQuery();
            while (rs.next()) {
                resultEmail = rs.getString("email");
                resultPassword = rs.getString("password");
                resultId = rs.getInt("id");
                resultFirstname = rs.getString("firstName");
                resultLastname = rs.getString("lastName");
                resultCreditCard = rs.getString("ccId");
                resultAddress = rs.getString("address");
            }

            JsonObject responseJsonObject = new JsonObject();
            if (email.equals(resultEmail) && password.equals(resultPassword)) {
                // Login success:

                // set this user into the session
                Customer currentUser = new Customer(resultId, resultEmail, resultPassword, resultFirstname,
                        resultLastname, resultCreditCard, resultAddress);
                request.getSession().setAttribute("user", currentUser);

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            } else {
                // TODO: add case for user exists but password is incorrect
                // Login fail
                responseJsonObject.addProperty("status", "fail");

                // Error messages to check if an account exists or not if the username and/or password is incorrect
                statementLogin.setString(1, email);
                //query = "select * from customers where email = '" + email + "';";
                rs = statement.executeQuery();
                String existingEmail = "";
                while (rs.next()) {
                    existingEmail = rs.getString("email");
                }
                if (!existingEmail.equals("")) {
                    responseJsonObject.addProperty("message", "user " + email + " doesn't exist");
                } else {
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            }
            response.getWriter().write(responseJsonObject.toString());
            rs.close();
            statement.close();
            statementLogin.close();
            dbcon.close();
        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        out.close();
    }
}
