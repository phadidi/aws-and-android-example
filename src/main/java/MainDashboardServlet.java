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
import java.sql.ResultSet;

// TODO: call add_movie stored procedure from sql using data in form from main_dashboard.html
@WebServlet(name = "MainDashboardServlet", urlPatterns = "/api/main_dashboard")
public class MainDashboardServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        try {
            String id = request.getParameter("movieId");
            String title = request.getParameter("movieTitle");
            String year = request.getParameter("movieYear");
            String director = request.getParameter("movieDirector");
            String star = request.getParameter("movieStar");
            String genre = request.getParameter("movieGenre");

            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            String resultId = "";
            String resultTitle = "";
            String resultDirector = "";

            PreparedStatement statementCheck = dbcon.prepareStatement("select id, title, director from movies where id = ? and title = ? and director = ?");
            statementCheck.setString(1, id);
            statementCheck.setString(2, title);
            statementCheck.setInt(3, Integer.parseInt(year));
            ResultSet rs = statementCheck.executeQuery();
            while (rs.next()) {
                resultId = rs.getString("id");
                resultTitle = rs.getString("title");
                resultDirector = rs.getString("director");
            }
            JsonObject responseJsonObject = new JsonObject();

            if (!resultId.equals(id) || !resultTitle.equals(title) || !resultDirector.equals(director)) {
                // No duplicate movie, add this movie to Fabflix moviedb + Declare our statement
                PreparedStatement statementAdd = dbcon.prepareStatement("call add_movie(?,?,?,?,?,?)");
                statementAdd.setString(1, id);
                statementAdd.setString(2, title);
                statementAdd.setInt(3, Integer.parseInt(year));
                statementAdd.setString(4, director);
                statementAdd.setString(5, star);
                statementAdd.setString(6, genre);
                statementAdd.executeQuery();
                statementAdd.close();

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
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
