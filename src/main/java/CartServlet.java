package main.java;

import com.google.gson.JsonArray;
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
import java.util.ArrayList;

/**
 * This CartServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
    /**
     * handles GET requests to store session information
     */
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        PrintWriter out = response.getWriter();
        long lastAccessTime = session.getLastAccessedTime();

        /*JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());
        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());*/

        // added some functionalities found in POST so cart loads without having to click add
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        Customer currentUser = (Customer) request.getSession().getAttribute("user");
        if (previousItems == null) {
            previousItems = currentUser.getCart();
            // TODO: bypass this casting issue
            session.setAttribute("previousItems", previousItems);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousItems) {
                previousItems = currentUser.getCart();
            }
        }

        try {
            Connection dbcon = dataSource.getConnection();

            // Construct a query to retrieve every movie whose id is in currentUser.cart
            String items = "(";
            for (String s : previousItems) {
                items += "'" + s + "',";
            }
            items = items.substring(0, items.length() - 1); // remove last comma
            items += ")";

            String query = "select m.id, m.title as title, m.year as year, m.director, \n" +
                    "group_concat(distinct g.name ORDER BY g.name separator ', ') as genrenames, \n" +
                    "group_concat(distinct concat(s.name, '_', s.id) order by (select count(sim.starId) as moviesIn from stars_in_movies sim where s.id = sim.starId group by sim.starID) DESC, s.name ASC SEPARATOR ',') AS starNamesAndIds\n" +
                    "from stars s, genres g, movies m, stars_in_movies sim, genres_in_movies gim \n" +
                    "where s.id = sim.starId and m.id = sim.movieId \n" +
                    "and g.id = gim.genreId and m.id = gim.movieId \n" +
                    "and m.id in " + items + " \n" +
                    "group by m.id;";

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

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", movieId);
                jsonObject.addProperty("title", movieTitle);
                jsonObject.addProperty("year", movieYear);
                jsonObject.addProperty("director", movieDirector);
                jsonObject.addProperty("genres", movieGenres);
                jsonObject.addProperty("starNamesAndIds", movieStars);

                String ratingsQuery = "select rating from ratings where movieId = '" + movieId + "';";
                Statement statementRatings = dbcon.createStatement();
                ResultSet rsRatings = statementRatings.executeQuery(ratingsQuery);
                String movieRating = "N/A";
                while (rsRatings.next()) {
                    String tempRating = rsRatings.getString("rating");
                    if (tempRating != null)
                        if (!tempRating.isEmpty())
                            movieRating = tempRating;
                }
                rsRatings.close();
                statementRatings.close();
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

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();
    }
}
