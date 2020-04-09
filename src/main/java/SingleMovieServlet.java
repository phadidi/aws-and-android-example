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

@WebServlet("/movie")

public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // change this to your own mysql username and password
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/fabflix_db";

        // set response mime type
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

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
            String movieId = request.getParameter("action");
            String query = "select m.id, m.title as title, m.year as year, m.director, group_concat(distinct g.name separator ', ')\n" +
                    "as genres, group_concat(distinct s.name separator ', ') as stars, (SELECT rating from ratings r where m.id = r.movieId) as rating\n" +
                    "from stars s, genres g, movies m, stars_in_movies sim, genres_in_movies gim\n" +
                    "where s.id = sim.starId and m.id = sim.movieId\n" +
                    "and g.id = gim.genreId and m.id = gim.movieId\n" +
                    "and m.id = '" + movieId + "';";
            // execute query
            ResultSet resultSet = statement.executeQuery(query);

            out.println("<body>");
            out.println("<h1>Single Movie Page</h1>");


            // add a row for every star result
            while (resultSet.next()) {
                // get a star from result set
                String title = resultSet.getString("title");
                String year = resultSet.getString("year");
                String director = resultSet.getString("director");
                String genres = resultSet.getString("genres");
                String stars = resultSet.getString("stars");
                String rating = resultSet.getString("rating");

                out.println("<p>Title: " + title + "</p>");
                if (year == null)
                    out.println("<p>Year: N/A</p>");
                else
                    out.println("<p>Year: " + year + "</p>");
                out.println("<p>Director: " + director + "</p>");
                out.print("<p>Genre(s):</p>");
                String[] genresSplit = genres.split(",");
                out.print("<ul>");
                for (String m : genresSplit) {
                    out.print("<li>" + m + "</li>");
                }
                out.println("</ul>");

                out.print("<p>Star(s):</p>");
                String[] starsSplit = stars.split(",");
                out.print("<ul>");
                for (String s : starsSplit) {
                    if (s.startsWith(" ")) {
                        s = s.substring(1, s.length());
                    }
                    out.print("<li><a href='starlist?action=" + s + "'>" + s + "</a></li>");
                }
                out.println("</ul>");

                if(rating == null)
                    out.println("<p>rating: N/A</p>");
                else
                    out.println("<p>Rating: " + rating + "</p>");

                out.println("<p><a href='/cs122b-spring20-team-13/'>Return to Movie List</a></p>");
            }

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
