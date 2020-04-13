package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


// this annotation maps this Java Servlet Class to a URL
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movielist")

public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // change this to your own mysql username and password
        //String loginUser = "mytestuser";
        //String loginPasswd = "mypassword";
        //String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        // set response mime type
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        //out.println("<html>");
        //out.println("<head><title>Fabflix</title></head>");

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            // create database connection
            //Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            Connection dbcon = dataSource.getConnection();
            // declare statement + prepare query
            //Statement statement = connection.createStatement();
            String query = "select m.id, m.title, m.year, m.director, \n" +
                    "(group_concat(distinct g.name separator ',')) as genres,\n" +
                    "(group_concat(distinct s.name  separator ','))  as stars, \n" +
                    "r.rating \n" +
                    "from movies m, genres g,  genres_in_movies gim, stars s, stars_in_movies sim, ratings r\n" +
                    "where m.id=gim.movieId and \n" +
                    "gim.genreId = g.Id and\n" +
                    "m.id=sim.movieId and\n" +
                    "sim.starId=s.id and \n" +
                    "m.id = r.movieId\n" +
                    "group by m.title, m.year, m.director, r.rating\n" +
                    "order by r.rating DESC\n" +
                    "limit 20;";
            PreparedStatement dbstatement = dbcon.prepareStatement(query);
            // execute query
            //ResultSet resultSet = statement.executeQuery(query);
            ResultSet movieResults = dbstatement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while (movieResults.next()) {
                String movieId = movieResults.getString("id");
                String title = movieResults.getString("title");
                String year = movieResults.getString("year");
                String director = movieResults.getString("director");
                String genresConcat = movieResults.getString("genres");
                String starsConcat = movieResults.getString("stars");
                String rating = movieResults.getString("rating");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", movieId);
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
                //TODO: split the genres and stars up to three entries again
                jsonObject.addProperty("genres", genresConcat);
                jsonObject.addProperty("stars", starsConcat);
                jsonObject.addProperty("rating", rating);
                jsonArray.add(jsonObject);
            }

            /*out.println("<body>");
            out.println("<h1>Top 20 Movies List</h1>");

            out.println("<table border>");

            // add table header row
            out.println("<tr>");
            out.println("<td>title</td>");
            out.println("<td>year</td>");
            out.println("<td>director</td>");
            out.println("<td>genres</td>");
            out.println("<td>stars</td>");
            out.println("<td>rating</td>");
            out.println("</tr>");*/

            // add a row for every star result
            /*while (resultSet.next()) {
                String movieId = resultSet.getString("id");
                String title = resultSet.getString("title");
                String year = resultSet.getString("year");
                String director = resultSet.getString("director");
                String genresConcat = resultSet.getString("genres");
                String starsConcat = resultSet.getString("stars");
                String rating = resultSet.getString("rating");
                out.println("<tr>");
                out.println("<td><a href='movie?action=" + movieId + "'>" + title + "</a></td>");
                out.println("<td>" + year + "</td>");
                out.println("<td>" + director + "</td>");
                out.println("<td>" + genresConcat + "</td>");
                String[] starsSplit = starsConcat.split(",");
                out.print("<td>");
                int count = 1; // added to remove trailing comma at the end
                int lim = starsSplit.length;
                for (String s : starsSplit) {
                    //out.print(s + ", ");
                    if (count < lim && count < 3)
                        out.print("<a href='starlist?action=" + s + "'>" + s + "</a>" + ", ");
                    else { // reached the end, remove trailing comma
                        out.print("<a href='starlist?action=" + s + "'>" + s + "</a>");
                        break; // stop after 3 stars without the delay of calling substring_index in SQL
                    }
                    count++;
                }
                out.println("</td>");
                out.println("<td>" + rating + "</td>");
                out.println("</tr>");

            }*/

            //out.println("</table>");
            //out.println("</body>");

            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            movieResults.close();
            dbstatement.close();
            dbcon.close();

            //resultSet.close();
            //statement.close();
            //connection.close();

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
            //e.printStackTrace();

            //out.println("<body>");
            //out.println("<p>");
            //out.println("Exception in doGet: " + e.getMessage());
            //out.println("</p>");
            //out.print("</body>");
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }

        //out.println("</html>");
        out.close();
    }

}
