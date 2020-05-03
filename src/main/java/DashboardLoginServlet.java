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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// TODO: Implement HTTPS to dashboard once successfully configured!
@WebServlet(name = "DashboardLoginServlet", urlPatterns = "/api/_dashboard")
public class DashboardLoginServlet extends HttpServlet {
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

        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
            String resultEmail = "";
            String resultPassword = "";
            String resultFullname = "";

            // Declare our statement
            PreparedStatement statementLogin = dbcon.prepareStatement("select * from employees where email = ? and password = ?;");
            statementLogin.setString(1, email);
            statementLogin.setString(2, password);
            ResultSet rs = statementLogin.executeQuery();
            while (rs.next()) {
                resultEmail = rs.getString("email");
                resultPassword = rs.getString("password");
                resultFullname = rs.getString("fullname");

            }

            JsonObject responseJsonObject = new JsonObject();
            if (email.equals(resultEmail) && password.equals(resultPassword)) {
                // Login success:

                // set this user into the session
                Employee currentEmployee = new Employee(resultEmail, resultPassword, resultFullname);
                request.getSession().setAttribute("employee", currentEmployee);

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            } else {
                // TODO: add case for employee exists but password is incorrect
                // Login fail
                responseJsonObject.addProperty("status", "fail");

                // Error messages to check if an account exists or not if the username and/or password is incorrect
                PreparedStatement statementFail = dbcon.prepareStatement("select * from employee where email = ?;");
                statementFail.setString(1, email);
                rs = statementFail.executeQuery();
                String existingEmail = "";
                while (rs.next()) {
                    existingEmail = rs.getString("email");
                }
                if (existingEmail.equals("")) {
                    responseJsonObject.addProperty("message", "employee " + email + " doesn't exist");
                } else {
                    responseJsonObject.addProperty("message", "incorrect password");
                }
                statementFail.close();
            }
            response.getWriter().write(responseJsonObject.toString());
            rs.close();
            statementLogin.close();
            dbcon.close();
        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();
    }
}
