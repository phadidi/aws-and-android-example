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
import java.util.Map;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
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
        Map<String, Integer> previousItems = (Map<String, Integer>) session.getAttribute("previousItems");
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

            Statement statement = dbcon.createStatement();
            ResultSet rs;
            JsonArray jsonArray = new JsonArray();

            for (Map.Entry<String, Integer> val : previousItems.entrySet()){
                // for debugging purposes
                System.out.println(val.getKey() + ": " + val.getValue());

                String query = "select id, title from movies where id = '" + val.getKey() + "'";
                //System.out.println(query);

                // Declare Statement
                rs = statement.executeQuery(query);

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
            }
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        try {
            String first_name = request.getParameter("fname");
            log(first_name);
            String last_name = request.getParameter("lname");
            log(last_name);
            String ccnumber = request.getParameter("ccnumber");
            log(ccnumber);
            String exp_date = request.getParameter("exp_date");
            log(exp_date);


            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement statement = dbcon.createStatement();

        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
            //String resultEmail = "";
            //String resultPassword = "";
            //int resultId = (int) (Math.random() * 1000000);
            String resultFirstname = "";
            String resultLastname = "";
            String resultCreditCard = "";
            String resultExpirationDate = "";


            String query = "select * from creditcards where firstName = '" + first_name
                            + "' and lastName = '" + last_name
                            + "' and id = '" + ccnumber
                            + "' and expiration = '" + exp_date + "';";
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                //resultEmail = rs.getString("email");
                //resultPassword = rs.getString("password");
                //resultId = rs.getInt("id");
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

                // Error messages to check if an account exists or not if the username and/or password is incorrect
//                query = "select * from customers where email = '" + email + "';";
//                rs = statement.executeQuery(query);
//                String existingEmail = "";
//                while (rs.next()) {
//                    existingEmail = rs.getString("email");
//                }
//                if (!existingEmail.equals("")) {
//                    responseJsonObject.addProperty("message", "user " + email + " doesn't exist");
//                } else {
//                    responseJsonObject.addProperty("message", "incorrect password");
//                }
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

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        out.close();
    }
}
