package main.java;

import com.google.gson.JsonArray;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

// server endpoint URL
@WebServlet("/movie-suggestion")
public class MovieSuggestion extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public MovieSuggestion() {
        super();
    }

    //@Resource(name = "jdbc/moviedb")
    //private DataSource dataSource;

    private static JsonObject generateJsonObject(String movieID, String movieTitle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle);

        JsonObject additionalDataJsonObject = new JsonObject();
        System.out.println(movieID);
        additionalDataJsonObject.addProperty("movieID", movieID);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }

    public String getServletInfo() {
        return "Movie Suggestion Servlet handles autocomplete searches on the Main Page";
    }

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

            // setup the response json array
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                out.write(jsonArray.toString());
                return;
            }

            //Connection dbcon = dataSource.getConnection();

            String stringForFullTextSearch = splitSearchString(query);

            String sqlquery = "select id, title from movies where match(title) against(? in boolean mode) limit 10;";

            PreparedStatement statement = dbcon.prepareStatement(sqlquery);

            statement.setString(1, stringForFullTextSearch);

            ResultSet rs = statement.executeQuery();

            String id = "";

            String title = "";

            while (rs.next()) {
                id = rs.getString("id");
                title = rs.getString("title");
                jsonArray.add(generateJsonObject(id, title));
            }

            out.write(jsonArray.toString());
            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    public String splitSearchString(String q) {
        if (q != null) {
            String[] split = q.split(" ");

            ArrayList<String> result = new ArrayList<>();

            for (String temp : split) {
                temp = "+" + temp + "*";
                result.add(temp);
            }
            String resultString = String.join(" ", result);
            return resultString;
        }
        return null;
    }
}
