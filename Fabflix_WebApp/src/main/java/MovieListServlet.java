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
import java.util.Enumeration;


// Declaring a WebServlet called MovieListServlet, which maps to url "/api/movielist"
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

        String searchLetter = request.getParameter("letter");

        String searchTitle = request.getParameter("search_title");
        String searchYear = request.getParameter("search_year");
        String searchDirector = request.getParameter("search_director");
        String searchStar = request.getParameter("search_star");

        String sort = request.getParameter("sort");

        String genreName = request.getParameter("genre");

        String limit = request.getParameter("limit");
        Integer limit_num = Integer.parseInt(limit);

        String pageNumber = request.getParameter("page");
        Integer page = (Integer.parseInt(pageNumber) - 1) * limit_num;

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement

            String query = "select m.id, m.title, m.year, m.director,\n" +
                    "group_concat(distinct g.name ORDER BY g.name SEPARATOR ', ') AS genresname,\n" +
                    "group_concat(distinct concat(s.name, '_', s.id) order by (select count(sim.starId) as moviesIn from stars_in_movies sim where s.id = sim.starId group by sim.starID) DESC, s.name ASC SEPARATOR ',') AS starNamesAndIds,\n" +
                    "r.rating\n" +
                    "FROM (movies m, genres g, stars s, stars_in_movies sim, genres_in_movies gim)\n" +
                    "LEFT JOIN ratings r\n" +
                    "ON m.id = r.movieId\n" +
                    "WHERE m.id=gim.movieId AND\n" +
                    "gim.genreId = g.Id AND\n" +
                    "m.id=sim.movieId AND\n" +
                    "sim.starId=s.id\n";

            //TODO: implement boolean mode into MATCH AGAINST queries
            if (searchTitle != null) {
                query += "AND MATCH (m.title) AGAINST('" + searchTitle + "')\n";
            }

            if (searchYear != null) {
                query += "AND m.year=" + searchYear + "\n";
            }
            if (searchDirector != null) {
                query += "AND MATCH (m.director) AGAINST ('" + searchDirector + "')\n";
            }

            if (searchLetter != null) {
                if (searchLetter.equals("non_alphanumeric")) {
                    query += "AND m.title REGEXP '^[^0-9A-Za-z]'";
                } else {
                    query += "AND m.title like'" + searchLetter + "%'\n";
                }
            }
            //TODO: identify hoq to implement full-text with star names being concatenated with star ids
            query += "GROUP BY m.id\n";

            // use having clause to properly print out all genres and star names when selecting for one
            if (genreName != null) {
                query += "HAVING  genresname like \"%" + genreName + "%\"";
            }

            if (searchStar != null) {
                query += "HAVING  starNamesAndIds like \"%" + searchStar + "%\"";
            }

            if (sort.compareTo("title_asc_rating_asc") == 0) {
                query += "ORDER BY m.title asc, r.rating asc\n";
            } else if (sort.compareTo("title_desc_rating_desc") == 0) {
                query += "ORDER BY m.title DESC, r.rating DESC\n";
            } else if (sort.compareTo("rating_asc_title_asc") == 0) {
                query += "ORDER BY r.rating asc, m.title asc\n";
            } else if (sort.compareTo("rating_desc_title_DESC") == 0) {
                query += "ORDER BY r.rating DESC, m.title DESC\n";
            } else if (sort.compareTo("rating_asc_title_desc") == 0) {
                query += "ORDER BY r.rating asc, m.title DESC\n";
            } else if (sort.compareTo("rating_desc_title_asc") == 0) {
                query += "ORDER BY r.rating DESC, m.title ASC\n";
            } else if (sort.compareTo("title_asc_rating_desc") == 0) {
                query += "ORDER BY m.title asc, r.rating desc\n";
            } else if (sort.compareTo("title_desc_rating_asc") == 0) {
                query += "ORDER BY m.title desc, r.rating asc\n";
            }

            if (limit.compareTo("10") == 0) {
                query += "LIMIT 10 OFFSET " + Integer.toString(page) + ";";
            } else {
                query += "LIMIT " + limit + " OFFSET " + Integer.toString(page) + ";";
            }

            System.out.println(query);

            // Perform the query
            PreparedStatement statement = dbcon.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
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
                jsonObject.addProperty("starNamesAndIds", movie_starNamesAndIds);
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Enumeration<String> myParameters = request.getParameterNames();
        //System.out.println(myParameters.toString());
        String movieId = myParameters.nextElement();
        //System.out.println(movieId);

        log("adding '" + movieId + "' to cart\n");
        response.setContentType("application/json");
        HttpSession session = request.getSession();
        Customer currentUser = (Customer) session.getAttribute("user");
        currentUser.addToCart(movieId);
        session.setAttribute("user", currentUser);
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("status", "success");
        responseJsonObject.addProperty("message", "success");
        response.getWriter().write(responseJsonObject.toString());
    }
}