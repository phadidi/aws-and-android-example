package main.java;

import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet(name = "StarDashboardServlet", urlPatterns = "/api/_dashboard_star")
public class MainDashboardServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        try {
            String id = "";
            String name = request.getParameter("starName");
            String birthYear = request.getParameter("starBirthYear");

            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            if (name != null || !name.equals("")) {
                PreparedStatement statementAdd = dbcon.prepareStatement("INSERT into stars VALUES(?,?,?);");
                String idQuery = "SELECT CONCAT('nm', (select LPAD(substring((select max(id) from stars), 3) + 1, 7, '0'))) as starId;";
                PreparedStatement statementId = dbcon.prepareStatement(idQuery);
                resultSet rs = statementId.executeQuery();
                while (rs.next()) {
                    id = rs.getString("starId");
                }
                statementAdd.setString(1, id);
                statementAdd.setString(2, name);
                if (birthYear != null || !birthYear.equals(""))
                    statementAdd.setInt(3, Integer.parseInt(birthYear));
                else
                    statementAdd.setString(3, "N/A");
                statementAdd.executeUpdate();

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
                rs.close();
                statementId.close();
                statementAdd.close();
            } else {
                // add star fail
                responseJsonObject.addProperty("status", "fail");
                // Error message if a movie already exists
                responseJsonObject.addProperty("message", "star name cannot be blank");
            }

            response.getWriter().write(responseJsonObject.toString());
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
