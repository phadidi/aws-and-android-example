package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public String getServletInfo() {
        return "Confirmation Servlet handles the purchase confirmation process after successfully entering payment info";
    }

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

            //Context initCtx = new InitialContext();

            //Context envCtx = (Context) initCtx.lookup("java:comp/env");
            //if (envCtx == null)
                //out.println("envCtx is NULL");

            // Look up our data source
            //DataSource ds = (DataSource) envCtx.lookup("jdbc/masterdb");

            // the following commented lines are direct connections without pooling
            //Class.forName("org.gjt.mm.mysql.Driver");
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            //if (ds == null)
                //out.println("ds is null.");

            //Connection dbcon = ds.getConnection();
            //if (dbcon == null)
                //out.println("dbcon is null.");

            Connection dbcon = dataSource.getConnection();

            // Construct a query to retrieve every movie whose id is in currentUser.cart

            int customerId = currentUser.getId();

            ArrayList<String> salesIds = new ArrayList<String>();

            ResultSet rs;

            String testQuantity = "SELECT COUNT(*)\n" +
                    "FROM INFORMATION_SCHEMA.COLUMNS\n" +
                    "WHERE table_schema = 'moviedb'\n" +
                    "  AND table_name = 'sales';";
            PreparedStatement checkQuantity = dbcon.prepareStatement(testQuantity);
            ResultSet r = checkQuantity.executeQuery();
            int columnCount = 0;
            while (r.next()) {
                columnCount = r.getInt("COUNT(*)");
            }

            if (columnCount == 4) {
                String alterQuery = "alter table sales add column quantity int DEFAULT 1;";
                PreparedStatement alterStatement = dbcon.prepareStatement(alterQuery);
                int alterResult = alterStatement.executeUpdate();
                System.out.println("altering customers table schema completed, " + alterResult + " rows affected");
            }
            for (Map.Entry<String, Integer> val : previousItems.entrySet()) {

                String query = "INSERT INTO sales(customerId, movieId, saleDate, quantity) VALUES(" + customerId
                        + ", '" + val.getKey() + "'," + "CURDATE()" + "," + val.getValue()
                        + ");";

                PreparedStatement statement = dbcon.prepareStatement(query);
                statement.executeUpdate();

                String fetchLatest = "select * from sales where idsales=(SELECT LAST_INSERT_ID());";
                PreparedStatement getLatest = dbcon.prepareStatement(fetchLatest);
                rs = getLatest.executeQuery();

                while (rs.next()) {
                    salesIds.add(rs.getString("idsales"));
                }
                rs.close();
                statement.close();
                getLatest.close();
            }

            Statement statement1 = dbcon.createStatement();
            ResultSet rs1;
            JsonArray jsonArray = new JsonArray();

            String salesIds_string = String.join(",", salesIds);
            System.out.println(salesIds_string);

            String query1 = "select s.idsales, m.title, s.saleDate, s.quantity\n" +
                    "from sales s, movies m\n" +
                    "where s.movieId = m.id and s.customerId =" + customerId
                    + " and s.idsales in (" + salesIds_string + ");";
            System.out.println(query1);

            // Declare Statement
            rs1 = statement1.executeQuery(query1);

            String sId = "";
            String movieTitle = "";
            String date = "";
            String count = "";

            while (rs1.next()) {
                sId = rs1.getString("idsales");
                movieTitle = rs1.getString("title");
                date = rs1.getString("saleDate");
                count = rs1.getString("quantity");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("idsales", sId);
                jsonObject.addProperty("title", movieTitle);
                jsonObject.addProperty("saleDate", date);
                jsonObject.addProperty("quantity", count);
                jsonArray.add(jsonObject);
            }

            currentUser.checkoutCart();
            // write JSON string to output

            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs1.close();
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
