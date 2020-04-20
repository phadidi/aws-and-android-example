package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
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

// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("beginning of SingleMovieServlet\n");
        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Construct a query with parameter represented by "?"
            String query = "select m.id, m.title as title, m.year as year, m.director, \n" +
                    "group_concat(distinct g.name separator ', ') as genrenames, \n" +
                    "group_concat(distinct concat(s.name, '_', s.id) order by s.name SEPARATOR ',') AS starNamesAndIds \n" +
                    "from stars s, genres g, movies m, stars_in_movies sim, genres_in_movies gim \n" +
                    "where s.id = sim.starId and m.id = sim.movieId \n" +
                    "and g.id = gim.genreId and m.id = gim.movieId \n" +
                    "and m.id = '" + id + "';";

            // Declare our statement
            Statement statement = dbcon.createStatement();

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            //statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            String movieId = "";
            String movieTitle = "";
            String movieYear = "";
            String movieDirector = "";
            String movieGenres = "";
            String movieStars = "";

            // With one movie id, we are expecting to get up to one movie
            while (rs.next()) {

                movieId = rs.getString("id");
                movieTitle = rs.getString("title");
                movieYear = rs.getString("year");
                movieDirector = rs.getString("director");
                movieGenres = rs.getString("genrenames");
                movieStars = rs.getString("starNamesAndIds");
            }

            // Create a JsonObject based on the data we retrieve from rs

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", movieId);
            jsonObject.addProperty("title", movieTitle);
            jsonObject.addProperty("year", movieYear);
            jsonObject.addProperty("director", movieDirector);
            jsonObject.addProperty("genres", movieGenres);
            jsonObject.addProperty("stars", movieStars);

            query = "select rating from ratings where movieId = '" + id + "'";
            rs = statement.executeQuery(query);
            String movieRating = "N/A";
            while (rs.next()) {
                String tempRating = rs.getString("rating");
                if (tempRating != null)
                    if (!tempRating.isEmpty())
                        movieRating = tempRating;
            }
            jsonObject.addProperty("rating", movieRating);

            jsonArray.add(jsonObject);


            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

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
