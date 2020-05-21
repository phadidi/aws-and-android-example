package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")

public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;
    private String thisId;

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
        thisId = request.getParameter("id");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Construct a query with parameter represented by "?"
            /*String query = "select m.id, m.title as title, m.year as year, m.director, \n" +
                    "group_concat(distinct g.name ORDER BY g.name separator ', ') as genrenames, \n" +
                    "group_concat(distinct concat(s.name, '_', s.id) order by (select count(sim.starId) as moviesIn from stars_in_movies sim where s.id = sim.starId group by sim.starID) DESC, s.name ASC SEPARATOR ',') AS starNamesAndIds\n" +
                    "from stars s, genres g, movies m, stars_in_movies sim, genres_in_movies gim \n" +
                    "where s.id = sim.starId and m.id = sim.movieId \n" +
                    "and g.id = gim.genreId and m.id = gim.movieId \n" +
                    "and m.id = '" + thisId + "';";*/

            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement("select m.id, m.title, m.year, m.director,\n" +
                    "group_concat(distinct g.name ORDER BY g.name SEPARATOR ', ') AS genresname,\n" +
                    "group_concat(distinct concat(s.name, '_', s.id) order by (select count(sim.starId) as moviesIn from stars_in_movies sim where s.id = sim.starId group by sim.starID) DESC, s.name ASC SEPARATOR ',') AS starNamesAndIds,\n" +
                    "r.rating\n" +
                    "FROM (movies m, genres g, stars s, stars_in_movies sim, genres_in_movies gim)\n" +
                    "LEFT JOIN ratings r\n" +
                    "ON m.id = r.movieId\n" +
                    "WHERE m.id=gim.movieId AND\n" +
                    "gim.genreId = g.Id AND\n" +
                    "m.id=sim.movieId AND\n" +
                    "sim.starId=s.id\n" +
                    "AND m.id = ? \n" +
                    "GROUP BY m.id");
            statement.setString(1, thisId);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            //statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // With one movie id, we are expecting to get up to one movie
            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_genres = rs.getString("genresname");
                String movie_starNamesAndIds = rs.getString("starNamesAndIds");
                String movieRating = "N/A";
                String tempRating = rs.getString("rating");
                String price = "10.99";
                if (tempRating != null) {
                    movieRating = tempRating;
                }

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", movie_id);
                jsonObject.addProperty("title", movie_title);
                jsonObject.addProperty("year", movie_year);
                jsonObject.addProperty("director", movie_director);
                jsonObject.addProperty("genres", movie_genres);
                jsonObject.addProperty("stars", movie_starNamesAndIds);
                jsonObject.addProperty("rating", movieRating);
                jsonObject.addProperty("price", price);
                jsonArray.add(jsonObject);
            }


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

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();

    }

    //doPost will be used to add movie to cart
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //String movieId = request.getParameter("id");

        log("adding '" + thisId + "' to cart\n");
        response.setContentType("application/json");
        HttpSession session = request.getSession();
        Customer currentUser = (Customer) session.getAttribute("user");
        currentUser.addToCart(thisId);
        session.setAttribute("user", currentUser);
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("status", "success");
        responseJsonObject.addProperty("message", "success");
        response.getWriter().write(responseJsonObject.toString());
    }


}
