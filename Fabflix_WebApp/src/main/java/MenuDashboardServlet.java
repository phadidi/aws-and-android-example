package main.java;

import com.google.gson.JsonObject;

import javax.naming.Context;
import javax.naming.InitialContext;
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
import java.sql.SQLException;

@WebServlet(name = "MenuDashboardServlet", urlPatterns = "/api/_dashboard_menu")
public class MenuDashboardServlet extends HttpServlet {

    public String getServletInfo() {
        return "Menu Dashboard Servlet loads for an employee after logging in with the option to add a new movie or star to the database";
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    //@Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

            // Get a connection from dataSource
            //Connection dbcon = dataSource.getConnection();
            DatabaseMetaData databaseMetaData = dbcon.getMetaData();
            String output = "";

            String catalog = null, schemaPattern = null, tableNamePattern = null;
            String[] types = {"TABLE"};

            ResultSet rsTables = databaseMetaData.getTables(catalog, schemaPattern, tableNamePattern, types);

            while (rsTables.next()) {
                String tableName = rsTables.getString(3);
                output += ("<p>\n=== TABLE: " + tableName + "\n");

                String columnNamePattern = null;
                ResultSet rsColumns = databaseMetaData.getColumns(catalog, schemaPattern, tableName, columnNamePattern);

                ResultSet rsPK = databaseMetaData.getPrimaryKeys(catalog, schemaPattern, tableName);

                while (rsColumns.next()) {
                    String columnName = rsColumns.getString("COLUMN_NAME");
                    String columnType = rsColumns.getString("TYPE_NAME");
                    int columnSize = rsColumns.getInt("COLUMN_SIZE");
                    output += ("\t" + columnName + " - " + columnType + "(" + columnSize + ")\n");
                }

                while (rsPK.next()) {
                    String primaryKeyColumn = rsPK.getString("COLUMN_NAME");
                    output += ("\tPrimary Key Column: " + primaryKeyColumn + "\n");
                }
                output += "</p>\n";
                rsColumns.close();
                rsPK.close();
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("output", output);
            out.write(jsonObject.toString());
            response.setStatus(200);
            rsTables.close();
            dbcon.close();
        } catch (SQLException ex) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", ex.getMessage());
            out.write(jsonObject.toString());
            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
            ex.printStackTrace();
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
