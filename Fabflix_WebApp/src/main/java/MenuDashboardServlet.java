package main.java;

import com.google.gson.JsonObject;

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
            JsonObject jsonObject = new JsonObject();


            while (resultSet.next()) {
                out.write(resultSet.getString("movies"));
                out.write(resultSet.getString("stars"));
                out.write(resultSet.getString("stars_in_movies"));
                out.write(resultSet.getString("genres"));
                out.write(resultSet.getString("genres_in_movies"));
                out.write(resultSet.getString("customers"));
                out.write(resultSet.getString("creditcards"));
                out.write(resultSet.getString("sales"));
                out.write(resultSet.getString("ratings"));
                out.write(resultSet.getString("employees"));
            }

            String[] tables = {"movies", "stars", "stars_in_movies", "genres", "genres_in_movies", "customers", "creditcards", "sales", "ratings", "employees"};

            for (String t: tables) {

                ResultSet columns = databaseMetaData.getColumns(null, null, t, null);

                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String datatype = columns.getString("DATA_TYPE");
                    String columnsize = columns.getString("COLUMN_SIZE");
                    String decimaldigits = columns.getString("DECIMAL_DIGITS");
                    String isNullable = columns.getString("IS_NULLABLE");
                    String is_autoIncrment = columns.getString("IS_AUTOINCREMENT");
                    //Printing results
                    out.write(columnName + "---" + datatype + "---" + columnsize + "---" + decimaldigits + "---" + isNullable + "---" + is_autoIncrment);
                }

                ResultSet PK = databaseMetaData.getPrimaryKeys(null, null, t);
                out.println("------------PRIMARY KEYS-------------");
                while (PK.next()) {
                    out.write(PK.getString("COLUMN_NAME") + "===" + PK.getString("PK_NAME"));
                }

                ResultSet FK = databaseMetaData.getImportedKeys(null, null, t);
                out.println("------------FOREIGN KEYS-------------");
                while (FK.next()) {
                    out.write(FK.getString("PKTABLE_NAME") + "---" + FK.getString("PKCOLUMN_NAME") + "===" + FK.getString("FKTABLE_NAME") + "---" + FK.getString("FKCOLUMN_NAME"));
                }

                FK.close();
                PK.close();
                columns.close();
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
            }
            resultSet.close();
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
