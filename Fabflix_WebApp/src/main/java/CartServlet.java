package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.Context;
import javax.naming.InitialContext;
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
    //@Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;
    private String newQuantity;

    public String getServletInfo() {
        return "Cart Servlet handles a customer's shopping cart, including the ability to change item quantities";
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        PrintWriter out = response.getWriter();
        long lastAccessTime = session.getLastAccessedTime();

        // log sessionId and lastAccessTime
        log("sessionId: " + sessionId + ", lastAccessTime: " + lastAccessTime + "\n");

        // added some functionalities found in POST so cart loads without having to click add
        Map<String, Integer> previousItems = (Map<String, Integer>) session.getAttribute("previousItems");
        Customer currentUser = (Customer) request.getSession().getAttribute("user");
        if (previousItems == null) {
            previousItems = currentUser.getCart();
            session.setAttribute("previousItems", previousItems);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousItems) {
                previousItems = currentUser.getCart();
            }
        }

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


            //Connection dbcon = dataSource.getConnection();
            ResultSet rs;
            JsonArray jsonArray = new JsonArray();

            for (Map.Entry<String, Integer> val : previousItems.entrySet()) {
                // Construct a query to retrieve every movie whose id is in currentUser.cart
                String query = "select id, title from movies where id = ?";
                System.out.println(query);

                // Declare Statement
                PreparedStatement statement = dbcon.prepareStatement(query);
                statement.setString(1, val.getKey());
                rs = statement.executeQuery();

                String movieId = "";
                String movieTitle = "";
                String count = "";

                while (rs.next()) {
                    movieId = rs.getString("id");
                    movieTitle = rs.getString("title");
                    count = Integer.toString(val.getValue());

                    String newQuantity = request.getParameter(movieId);
                    if (newQuantity != null && newQuantity.compareTo(count) != 0) {
                        currentUser.changeQuantity(movieId, Integer.parseInt(newQuantity));
                        count = newQuantity;
                    }
                    System.out.println(movieId + " " + movieTitle + " " + count);

                    // Create a JsonObject based on the data we retrieve from rs
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("id", movieId);
                    jsonObject.addProperty("title", movieTitle);
                    jsonObject.addProperty("Quantity", count);
                    jsonArray.add(jsonObject);
                }
                // write JSON string to output
                statement.close();
                rs.close();
            }
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

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
