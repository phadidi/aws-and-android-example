package main.java;

import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "MenuDashboardServlet", urlPatterns = "/api/_dashboard_menu")
public class MenuDashboardServlet extends HttpServlet {

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        try {

            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            DatabaseMetaData databaseMetaData = dbcon.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null, null, "%", new String[]{"TABLE"});
            databaseMetaData.getColumns(null, null, "movies", null);
            databaseMetaData.getColumns(null, null, "stars", null);
            databaseMetaData.getColumns(null, null, "stars_in_movies", null);
            databaseMetaData.getColumns(null, null, "genres", null);
            databaseMetaData.getColumns(null, null, "genres_in_movies", null);
            databaseMetaData.getColumns(null, null, "customers", null);
            databaseMetaData.getColumns(null, null, "creditcards", null);
            databaseMetaData.getColumns(null, null, "sales", null);
            databaseMetaData.getColumns(null, null, "ratings", null);
            databaseMetaData.getColumns(null, null, "employees", null);



            JsonObject responseJsonObject = new JsonObject();

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");

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
