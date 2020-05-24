package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

// server endpoint URL
@WebServlet("/movie-suggestion")
public class MovieSuggestion extends HttpServlet {
    private static final long serialVersionUID = 1L;
    /*
     * populate the Super hero hash map.
     * Key is hero ID. Value is hero name.
     */
    public static HashMap<String, String> movieMap = new HashMap<>();

    static {
    }

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public MovieSuggestion() {
        super();
    }

    private static JsonObject generateJsonObject(String movieID, String movieTitle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle);

        JsonObject additionalDataJsonObject = new JsonObject();
        System.out.println(movieID);
        additionalDataJsonObject.addProperty("movieID", movieID);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // setup the response json array
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            Connection dbcon = dataSource.getConnection();

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

            response.getWriter().write(jsonArray.toString());
            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    public String splitSearchString(String q) {
        if (q != null) {
            String split[] = q.split(" ");

            ArrayList<String> result = new ArrayList<String>();

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
