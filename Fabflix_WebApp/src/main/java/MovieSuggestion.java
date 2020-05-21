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

    static {
//        superHeroMap.put(1, "Blade");
//        superHeroMap.put(2, "Ghost Rider");
//        superHeroMap.put(3, "Luke Cage");
//        superHeroMap.put(4, "Silver Surfer");
//        superHeroMap.put(5, "Beast");
//        superHeroMap.put(6, "Thing");
//        superHeroMap.put(7, "Black Panther");
//        superHeroMap.put(8, "Invisible Woman");
//        superHeroMap.put(9, "Nick Fury");
//        superHeroMap.put(10, "Storm");
//        superHeroMap.put(11, "Iron Man");
//        superHeroMap.put(12, "Professor X");
//        superHeroMap.put(13, "Hulk");
//        superHeroMap.put(14, "Cyclops");
//        superHeroMap.put(15, "Thor");
//        superHeroMap.put(16, "Jean Grey");
//        superHeroMap.put(17, "Wolverine");
//        superHeroMap.put(18, "Daredevil");
//        superHeroMap.put(19, "Captain America");
//        superHeroMap.put(20, "Spider-Man");
    }

    /*
     * populate the Super hero hash map.
     * Key is hero ID. Value is hero name.
     */
    public static HashMap<String, String> movieMap = new HashMap<>();

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public MovieSuggestion() {
        super();
    }

    /*
     * Generate the JSON Object from hero to be like this format:
     * {
     *   "value": "Iron Man",
     *   "data": { "movieId": 11 }
     * }
     *
     */
    private static JsonObject generateJsonObject(String movieID, String movieTitle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle);

        JsonObject additionalDataJsonObject = new JsonObject();
        System.out.println(movieID);
        additionalDataJsonObject.addProperty("movieID", movieID);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }

    /*
     *
     * Match the query against superheroes and return a JSON response.
     *
     * For example, if the query is "super":
     * The JSON response look like this:
     * [
     * 	{ "value": "Superman", "data": { "heroID": 101 } },
     * 	{ "value": "Supergirl", "data": { "heroID": 113 } }
     * ]
     *
     * The format is like this because it can be directly used by the
     *   JSON auto complete library this example is using. So that you don't have to convert the format.
     *
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     *
     *
     */
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

            // search on superheroes and add the results to JSON Array
            // this example only does a substring match
            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars
            Connection dbcon = dataSource.getConnection();

            String stringForFullTextSearch = splitSearchString(query);

            String sqlquery = "select id, title from movies where match(title) against(? in boolean mode) limit 10;";

            PreparedStatement statement = dbcon.prepareStatement(sqlquery);

            statement.setString(1, stringForFullTextSearch);

            ResultSet rs = statement.executeQuery();

            String id = "";

            String title = "";

            while(rs.next()){
                id = rs.getString("id");
                title = rs.getString("title");
                //System.out.println(id);
                jsonArray.add(generateJsonObject(id, title));
            }

            response.getWriter().write(jsonArray.toString());
            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    public String splitSearchString(String q){
        if(q != null) {
            String[] split = q.split(" ");

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
