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

@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
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

            int customerId = currentUser.getId();

            Statement statement = dbcon.createStatement();
            ResultSet rs;

            for (Map.Entry<String, Integer> val : previousItems.entrySet()){

                int saleId = (int) (Math.random() * 10000);
                String query = "INSERT INTO sales VALUES(" + Integer.toString(saleId) + "," + Integer.toString(customerId)
                        + ", '" + val.getKey() + "'," + "CURDATE()" + "," + val.getValue()
                        + ")";

                System.out.println(query);
                // Declare Statement
                statement = dbcon.createStatement();
                rs = statement.executeQuery(query);
                rs.close();
            }

            statement.close();

            Statement statement1 = dbcon.createStatement();
            ResultSet rs1;
            JsonArray jsonArray = new JsonArray();


            String query1 = "select s.idsales, s.customerId, s.movieId, m.title, s.saleDate, s.quantity\n" +
                    "from sales s, movies m\n" +
                    "where s.movieId = m.id and s.customerId =" + Integer.toString(customerId);
            System.out.println(query1);

            // Declare Statement
            statement = dbcon.createStatement();
            rs1 = statement.executeQuery(query1);

            String sId = "";
            String cId = "";
            String movieId = "";
            String movieTitle = "";
            String date = ""
;           String count = "";

            while (rs1.next()) {
                sId = rs1.getString("idsales");
                cId = rs1.getString("customerId");
                movieId = rs1.getString("movieId");
                movieTitle = rs1.getString("title");
                date = rs1.getString("saleDate");
                count = rs1.getString("quantity");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("idsales", sId);
                jsonObject.addProperty("customerId", cId);
                jsonObject.addProperty("movieId", movieId);
                jsonObject.addProperty("title", movieTitle);
                jsonObject.addProperty("saleDate", date);
                jsonObject.addProperty("quantity", count);
                jsonArray.add(jsonObject);
            }
            // write JSON string to output
            rs1.close();

            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            statement1.close();

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
