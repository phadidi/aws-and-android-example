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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


// server endpoint URL
@WebServlet("/movie-suggestion")
public class MovieSuggestion extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /*
     * populate the Super hero hash map.
     * Key is hero ID. Value is hero name.
     */
    public static HashMap<String, String> movieMap = new HashMap<>();
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    // TODO: identify where to implement static

    public MovieSuggestion() throws SQLException {
        super();
    }

    /*
     * Generate the JSON Object from hero to be like this format:
     * {
     *   "value": "movieTitle",
     *   "data": { "movieId": movieId }
     * }
     *
     */
    private static JsonObject generateJsonObject(String movieId, String movieTitle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieId", movieId);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }

    /*
     *
     * Match the query against movie and return a JSON response.
     *
     * For example, if the query is "super":
     * The JSON response look like this:
     * [
     * 	{ "value": "movieTitle", "data": { "movieTitle": movieId, etc. } },
     * ]
     *
     * The format is like this because it can be directly used by the
     *   JSON auto complete library this example is using. So that you don't have to convert the format.
     *
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     *
     *
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // setup the response json array
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");
            String searchBody = request.getParameter("searchBody");

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            Connection dbcon = dataSource.getConnection();
            PreparedStatement getMovies = dbcon.prepareStatement(query); //TODO: process query from js into MovieSuggestion.java with LIMIT 10
            ResultSet rs = getMovies.executeQuery();

            // search on superheroes and add the results to JSON Array
            // this example only does a substring match
            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars

            while (rs.next()) {
                movieMap.put(rs.getString("id"), rs.getString("title"));
            }

            for (String id : movieMap.keySet()) {
                String movieId = movieMap.get(id);
                if (movieId.toLowerCase().contains(query.toLowerCase())) {
                    jsonArray.add(generateJsonObject(id, movieId));
                }
            }

            response.getWriter().write(jsonArray.toString());
            rs.close();
            getMovies.close();
            dbcon.close();
            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }


}
