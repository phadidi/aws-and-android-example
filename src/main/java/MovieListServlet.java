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


// Declaring a WebServlet called StarsServlet, which maps to url "/api/movielist"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        String genreName = request.getParameter("genre");

        String pageNumber = request.getParameter("page");
        Integer page = (Integer.parseInt(pageNumber) - 1) * 10;
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement statement = dbcon.createStatement();
            String query;
            // TODO: devise different queries for each context based on the given search parameters
            if (genreName.compareTo("") != 0) {
                query = "select m.id, m.title, m.year, m.director,\n" +
                        "group_concat(distinct g.name ORDER BY g.name SEPARATOR ', ') AS genresname,\n" +
                        "group_concat(distinct concat(s.name, '_', s.id) order by (select count(sim.starId) as moviesIn from stars_in_movies sim where s.id = sim.starId group by sim.starID) DESC, s.name ASC SEPARATOR ',') AS starNamesAndIds\n" +
                        "FROM movies m, genres g, stars s, stars_in_movies sim, genres_in_movies gim\n" +
                        "WHERE m.id=gim.movieId AND\n" +
                        "gim.genreId = g.Id AND\n" +
                        "m.id=sim.movieId AND\n" +
                        "sim.starId=s.id AND\n" +
                        "g.name='" + genreName + "'\n" +
                        "GROUP BY m.title, m.year, m.director\n" +
                        "ORDER BY m.title\n" +
                        "LIMIT 10 OFFSET " + Integer.toString(page) + ";";
            } else {
                query = "select m.id, m.title, m.year, m.director,\n" +
                        "group_concat(distinct g.name ORDER BY g.name SEPARATOR ', ') AS genresname,\n" +
                        "group_concat(distinct concat(s.name, '_', s.id) order by (select count(sim.starId) as moviesIn from stars_in_movies sim where s.id = sim.starId group by sim.starID) DESC, s.name ASC SEPARATOR ',') AS starNamesAndIds\n" +
                        "FROM movies m, genres g, stars s, stars_in_movies sim, genres_in_movies gim\n" +
                        "WHERE m.id in \n" +
                        "(select distinct movies.Id from movies, genres, genres_in_movies where movies.Id = genres_in_movies.movieId \n" +
                        "and genres.id = (select id from genres where name like '" + genreName + "') and genres_in_movies.genreId = genres.id) AND\n" +
                        "m.id=gim.movieId AND\n" +
                        "gim.genreId = g.Id AND\n" +
                        "m.id=sim.movieId AND\n" +
                        "sim.starId=s.id\n" +
                        "GROUP BY m.title, m.year, m.director\n" +
                        "ORDER BY m.title\n" +
                        "LIMIT 10 OFFSET " + Integer.toString(page) + ";";
            }


            //String query = "select id, title, year from movies;";
            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            Statement rating_statement = dbcon.createStatement();
            String rating_query = "";
            ResultSet rs_rating;
            // Iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_genres = rs.getString("genresname");
                String movie_starNamesAndIds = rs.getString("starNamesAndIds");
                String movieRating = "N/A";

                rating_query = "select rating from ratings where movieId = '" + movie_id + "'";
                rs_rating = rating_statement.executeQuery(rating_query);
                while (rs_rating.next()) {
                    String tempRating = rs_rating.getString("rating");
                    if (tempRating != null)
                        if (!tempRating.isEmpty())
                            movieRating = tempRating;
                }

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", movie_id);
                jsonObject.addProperty("title", movie_title);
                jsonObject.addProperty("year", movie_year);
                jsonObject.addProperty("director", movie_director);
                jsonObject.addProperty("genres", movie_genres);
                jsonObject.addProperty("starNamesAndIds", movie_starNamesAndIds);
                jsonObject.addProperty("rating", movieRating);

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

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        out.close();

    }
}
