package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.omg.CORBA.INTERNAL;

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
import java.util.HashMap;
import java.util.Map;

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
            Map<String, Integer> hm = new HashMap<String, Integer>();
            for (String s: previousItems){
                Integer j = hm.get(s);
                hm.put(s, (j == null) ? 1 : j + 1);
            }
            for (Map.Entry<String, Integer> val : hm.entrySet()){

                System.out.println(val.getKey() + ": " + val.getValue());

                String count = Integer.toString(val.getValue());
                String query = "select id, title from movies where id = '" + val.getKey() + "'";

                // Declare Statement
                Statement statement = dbcon.createStatement();
                ResultSet rs = statement.executeQuery(query);

                JsonArray jsonArray = new JsonArray();

                String movieId = "";
                String movieTitle = "";

                while (rs.next()) {
                    movieId = rs.getString("id");
                    movieTitle = rs.getString("title");

                    // Create a JsonObject based on the data we retrieve from rs
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("id", movieId);
                    jsonObject.addProperty("title", movieTitle);
                    jsonObject.addProperty("Quantity", count);
                    jsonArray.add(jsonObject);
                }

                // write JSON string to output
                out.write(jsonArray.toString());
                // set response status to 200 (OK)
                response.setStatus(200);

                rs.close();
                statement.close();

            }

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
