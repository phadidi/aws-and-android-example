package main.java;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/movietest")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // change this to your own mysql username and password
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/fabflix_db";

        // set response mime type
        response.setContentType("text/html");

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>Fabflix</title></head>");

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            // create database connection
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // declare statement
            Statement statement = connection.createStatement();
            // prepare query
            String query = "select m.title, m.year, m.director, \n" +
                    "substring_index((group_concat(distinct g.name separator ', ') ), ',', 3) as genres,\n" +
                    "substring_index((group_concat(distinct s.name  separator ', ') ), ',', 3) as stars, \t\n" +
                    "r.rating \n" +
                    "from movies m, genres g,  genres_in_movies gim, stars s, stars_in_movies sim, ratings r\n" +
                    "where m.id=gim.movieId and \n" +
                    "gim.genreId = g.Id and\n" +
                    "m.id=sim.movieId and\n" +
                    "sim.starId=s.id and \n" +
                    "m.id = r.movieId\n" +
                    "group by m.title, m.year, m.director, r.rating\n" +
                    "order by r.rating DESC\n" +
                    "limit 20";
            // execute query
            ResultSet resultSet = statement.executeQuery(query);

            out.println("<body>");
            out.println("<h1>MovieDB Stars</h1>");

            out.println("<table border>");

            // add table header row
            /*out.println("<tr>");
            out.println("<td>id</td>");
            out.println("<td>name</td>");
            out.println("<td>birth year</td>");
            out.println("</tr>");*/

            // add a row for every star result
            while (resultSet.next()) {
                // get a star from result set
                /*String starID = resultSet.getString("id");
                String starName = resultSet.getString("name");
                String birthYear = resultSet.getString("birthyear");

                out.println("<tr>");
                out.println("<td>" + starID + "</td>");
                out.println("<td>" + starName + "</td>");
                out.println("<td>" + birthYear + "</td>");
                out.println("</tr>");*/
            }

            out.println("</table>");
            out.println("</body>");

            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            /*
             * After you deploy the WAR file through tomcat manager webpage,
             *   there's no console to see the print messages.
             * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
             *
             * To view the last n lines (for example, 100 lines) of messages you can use:
             *   tail -100 catalina.out
             * This can help you debug your program after deploying it on AWS.
             */
            e.printStackTrace();

            out.println("<body>");
            out.println("<p>");
            out.println("Exception in doGet: " + e.getMessage());
            out.println("</p>");
            out.print("</body>");
        }

        out.println("</html>");
        out.close();

    }
}