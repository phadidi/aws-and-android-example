package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;

// Declaring a WebServlet called MovieListServlet, which maps to url "/api/movielist"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public String getServletInfo() {
        return "Movie List Servlet takes in a set of parameters to build a SQL query, executes that query, and loads the results. " +
                " Each loaded movie can also be added to the customer's cart.";
    }

    // Create a dataSource which registered in web.xml
    //@Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Time an event in a program to nanosecond precision
        long startTime = System.nanoTime();
        /////////////////////////////////
        /// ** part to be measured ** ///
        /////////////////////////////////

        response.setContentType("application/json"); // Response mime type

        String searchLetter = request.getParameter("letter");

        String searchTitle = splitSearchString(request.getParameter("search_title"));
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
        // Time an event in a program to nanosecond precision
        long JDBCstartTime = System.nanoTime();
        /////////////////////////////////
        /// ** part to be measured ** ///
        /////////////////////////////////
        try {
            // the following few lines are for connection pooling
            // Obtain our environment naming context

            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

            // the following commented lines are direct connections without pooling
            //Class.forName("org.gjt.mm.mysql.Driver");
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            if (ds == null)
                out.println("ds is null.");

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                out.println("dbcon is null.");

            // Get a connection from dataSource
            //Connection dbcon = dataSource.getConnection();


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

            if (searchTitle != null) {
                query += "AND MATCH (m.title) AGAINST('" + searchTitle + "' in boolean mode)\n";
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

            query += "GROUP BY m.id\n";

            // use having clause to properly print out all genres and star names when selecting for one
            if (genreName != null) {
                query += "HAVING genresname like \"%" + genreName + "%\"\n";
            }

            if (searchStar != null) {
                query += "HAVING starNamesAndIds like \"%" + searchStar + "%\"\n";
                // NOTE: Fuzzy Search could be implemented here down the line
                //OR SIMILAR TO('" + searchStar + "', starNamesAndIds, 2)
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
                query += "LIMIT 10 OFFSET " + page + ";";
            } else {
                query += "LIMIT " + limit + " OFFSET " + page + ";";
            }

            System.out.println(query);
            request.setAttribute("query", query);

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
        long endTime = System.nanoTime();
        long tj = endTime - JDBCstartTime;
        long ts = endTime - startTime; // elapsed time in nano seconds. Note: print the values in nano seconds

        String contextPath = getServletContext().getRealPath("/");
        String txtFilePath = contextPath + "\\time_log.txt";

        try {
            File myfile = new File(txtFilePath);
            if (myfile.createNewFile()) {
                System.out.println("File created: " + myfile.getName());
            } else {
                System.out.println("File already exists.");
            }

            log("total time for JDBC in doGet: " + tj);
            log("total time for doGet: " + ts);

            // write to time_log.txt
            myfile.setWritable(true);
            FileWriter w = new FileWriter(myfile, true);
            w.write("{\"tj\": " + Long.toString(tj) + ", \"ts\": " + Long.toString(ts) + "}\n");
            w.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Enumeration<String> myParameters = request.getParameterNames();
        String movieId = myParameters.nextElement();

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

    public String splitSearchString(String q) {
        if (q != null) {
            String[] split = q.split(" ");

            ArrayList<String> result = new ArrayList<String>();

            for (String temp : split) {
                temp = "+" + temp + "*";
                result.add(temp);
            }
            String resultString = String.join(" ", result);
            return resultString;
        }
        return null;
    }
}