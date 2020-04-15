package main.java;

import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */

// Declaring a WebServlet called LoginServlet, which maps to url "/login"
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    // Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session;
        response.setContentType("text/html");    // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Building page head with title
        out.println("<html><head><title>Welcome to Fabflix</title></head>");

        // Building page body
        out.println("<body><h1>Enter your email and password</h1>");
        try {
            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            // TODO: convert login.html to index.html and the current index.html to movielist.html
            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
            String email = request.getParameter("email");
            String password = request.getParameter("password");
			JsonObject responseJsonObject = new JsonObject();

            // Generate a SQL query
            String query = String.format("SELECT * from customers where email = '%s' and password = '%s';", email, password);

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            String c_ID = "";
            String c_Name = "";
            while (rs.next()) {
                c_ID = rs.getString("email");
                c_Name = rs.getString("firstName") + " " + rs.getString("lastName");
            }
            // If an ID is found, redirect to the main page
            if (c_ID.compareTo("") != 0) {
                response.sendRedirect("index.html");
				// TODO: flesh out Customer object as attributes are added
                session = request.getSession(true);
                session.setAttribute("customer", new Customer(c_ID));
                session.setMaxInactiveInterval(30); // 30 seconds
				responseJsonObject.addProperty("status", "success");
				responseJsonObject.addProperty("message", "success");
                response.sendRedirect("index.html");
            } else { // TODO: split into username and password checks
			responseJsonObject.addProperty("status", "fail");
			responseJsonObject.addProperty("message", "incorrect email and/or password");
			}
			response.getWriter().write(responseJsonObject.toString());

            // Close all structures
            rs.close();
            statement.close();
            dbCon.close();

        } catch (Exception ex) {

            // Output Error Massage to html
            out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", ex.getMessage()));
            return;
        }
        out.close();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        doGet(request, response);
    }
}
