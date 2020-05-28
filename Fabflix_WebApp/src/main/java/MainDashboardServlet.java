package main.java;

import com.google.gson.JsonObject;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "MainDashboardServlet", urlPatterns = "/api/_dashboard_main")
public class MainDashboardServlet extends HttpServlet {
    public String getServletInfo() {
        return "Main Dashboard Servlet adds a movie to the SQL database with an employee request";
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */

    //@Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;
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

            String title = request.getParameter("movieTitle");
            String year = request.getParameter("movieYear");
            String director = request.getParameter("movieDirector");
            String star = request.getParameter("movieStar");
            String genre = request.getParameter("movieGenre");

            // Get a connection from dataSource
            //Connection dbcon = dataSource.getConnection();
            String resultTitle = "";
            String resultDirector = "";
            String resultYear = "";

            PreparedStatement statementCheck = dbcon.prepareStatement("select title, director, year from movies where title = ? and director = ? and year = ?");
            statementCheck.setString(1, title);
            statementCheck.setString(2, director);
            statementCheck.setInt(3, Integer.parseInt(year));
            ResultSet rs = statementCheck.executeQuery();
            while (rs.next()) {
                resultTitle = rs.getString("title");
                resultDirector = rs.getString("director");
                resultYear = rs.getString("year");
            }
            JsonObject responseJsonObject = new JsonObject();

            if (!resultTitle.equals(title) || !resultDirector.equals(director) || !resultYear.equals(year)) {
                // No duplicate movie, add this movie to Fabflix moviedb + Declare our statement
                PreparedStatement statementAdd = dbcon.prepareStatement("call add_movie(?,?,?,?,?)");
                statementAdd.setString(1, title);
                statementAdd.setInt(2, Integer.parseInt(year));
                statementAdd.setString(3, director);
                statementAdd.setString(4, star);
                statementAdd.setString(5, genre);
                statementAdd.executeQuery();
                statementAdd.close();
                PreparedStatement statementId = dbcon.prepareStatement("select id from movies where title = ? and year = ? and director = ?");
                statementId.setString(1, title);
                statementId.setInt(2, Integer.parseInt(year));
                statementId.setString(3, director);
                ResultSet rsId = statementId.executeQuery();
                String resultId = "";
                while (rsId.next()) {
                    resultId = rsId.getString("id");
                }

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "added " + resultId + " to cart");
                rsId.close();
                statementId.close();

            } else {
                // add_movie fail
                responseJsonObject.addProperty("status", "fail");
                // Error message if a movie already exists
                responseJsonObject.addProperty("message", "movie already exists");
            }
            response.getWriter().write(responseJsonObject.toString());
            rs.close();
            statementCheck.close();
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
