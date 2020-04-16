package main.java;

import com.google.gson.JsonObject;

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
import java.sql.Statement;

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
            Statement statement = dbcon.createStatement();

        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
            String resultEmail = "";
            String resultPassword = "";

            String query = "select * from customers where email = '" + email + "' and password = '" + password + "';";
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                resultEmail = rs.getString("email");
                resultPassword = rs.getString("password");
            }

            JsonObject responseJsonObject = new JsonObject();
            if (email.equals(resultEmail) && password.equals(resultPassword)) {
                // Login success:

                // set this user into the session
                request.getSession().setAttribute("user", new Customer(email));

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");

                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                if (!email.equals("mytestuser")) {
                    responseJsonObject.addProperty("message", "user " + email + " doesn't exist");
                } else {
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            }
            response.getWriter().write(responseJsonObject.toString());
            rs.close();
            statement.close();
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
