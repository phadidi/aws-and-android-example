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

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public String getServletInfo() {
        return "Payment Servlet asks a customer to confirm payment info before checking out the items in a cart";
    }

    // Create a dataSource which registered in web.xml
    //@Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        PrintWriter out = response.getWriter();
        long lastAccessTime = session.getLastAccessedTime();
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
                String query = "select id, title from movies where id = '" + val.getKey() + "'";
                // Declare Statement
                PreparedStatement statement = dbcon.prepareStatement(query);
                rs = statement.executeQuery();

                String movieId = "";
                String movieTitle = "";
                String count = "";

                while (rs.next()) {
                    movieId = rs.getString("id");
                    movieTitle = rs.getString("title");
                    count = Integer.toString(val.getValue());

                    // Create a JsonObject based on the data we retrieve from rs
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("id", movieId);
                    jsonObject.addProperty("title", movieTitle);
                    jsonObject.addProperty("Quantity", count);
                    jsonArray.add(jsonObject);
                }
                rs.close();
                statement.close();
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
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

            String first_name = request.getParameter("fname");
            log("first_name: " + first_name + "\n");
            String last_name = request.getParameter("lname");
            log("last_name:  " + last_name + "\n");
            String ccnumber = request.getParameter("ccnumber");
            log("ccnumber: " + ccnumber + "\n");
            String exp_date = request.getParameter("exp_date");
            log("exp_date: " + exp_date + "\n");

            // Get a connection from dataSource
            //Connection dbcon = dataSource.getConnection();

            String resultFirstname = "";
            String resultLastname = "";
            String resultCreditCard = "";
            String resultExpirationDate = "";

            // used preparedStatement for security reasons
            PreparedStatement statement = dbcon.prepareStatement("select * from creditcards where firstName = ? and lastName = ? and id = ? and expiration = ?;");
            statement.setString(1, first_name);
            statement.setString(2, last_name);
            statement.setString(3, ccnumber);
            statement.setString(4, exp_date);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                resultFirstname = rs.getString("firstName");
                resultLastname = rs.getString("lastName");
                resultCreditCard = rs.getString("id");
                resultExpirationDate = rs.getString("expiration");
            }

            JsonObject responseJsonObject = new JsonObject();
            if (first_name.equals(resultFirstname) && last_name.equals(resultLastname)
                    && ccnumber.equals(resultCreditCard) && exp_date.equals(resultExpirationDate)) {
                // Card accepted:

                // set this user into the session
                Customer currentUser = (Customer) request.getSession().getAttribute("user");

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            } else {
                // Card declined
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Card information is incorrect");
            }
            response.getWriter().write(responseJsonObject.toString());
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
