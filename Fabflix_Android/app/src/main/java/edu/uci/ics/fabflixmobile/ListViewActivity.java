package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListViewActivity extends Activity {
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        //this should be retrieved from the database and the backend server

        final ArrayList<Movie> movies = new ArrayList<>();

        url = "https://10.0.2.2:8443/cs122b-spring20-team-13/api/";

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest loginRequest = new StringRequest(Request.Method.GET, url + "movielist?page=1&sort=title_asc_rating_asc&limit=10&search_title=spider", response -> {
            //TODO should parse the json response to redirect to appropriate functions.
            Log.d("list.success", response);
            System.out.println(response.getClass().getName());

            try{
                JSONArray jsonArray = new JSONArray(response);

                // Getting JSON Array node
                for(int i = 0; i < jsonArray.length(); i++){
                    final JSONObject m = jsonArray.getJSONObject(i);
                    String id = m.getString("id");
                    String title = m.getString("title");
                    String year = m.getString("year");
                    String director = m.getString("director");
                    movies.add(new Movie(id, title, Integer.parseInt(year), director));
                }
            }
            catch(final JSONException e){
                Log.e("Json parsing error", e.toString());
            }
        },
                error -> {
                    // error
                    Log.d("list.error", error.toString());
                });

//        movies.add(new Movie("tt123","The Terminal", (short) 2004, "ME"));
//        movies.add(new Movie("tt124","The Final Season", (short) 2007, "ME"));

        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
                String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getTitle(), movie.getYear());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(loginRequest);
    }
}



//package edu.uci.ics.fabflixmobile;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import javax.annotation.Resource;
//import javax.sql.DataSource;
//import java.sql.*;
//import java.util.ArrayList;
//
//public class ListViewActivity extends Activity {
//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.listview);
//        //this should be retrieved from the database and the backend server
//        final ArrayList<Movie> movies = new ArrayList<>();
//
//        try {
//            //TODO: correctly implement a mysql connection for future movielist queries
//            Connection dbcon = DriverManager.getConnection("jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false", "mytestuser", "mypassword");
//            PreparedStatement statementId = dbcon.prepareStatement("select * from movies limit 5;");
//            ResultSet rs = statementId.executeQuery();
//            while (rs.next()) {
//                movies.add(
//                        new Movie(rs.getString("id"), rs.getString("title"),
//                                rs.getInt("year"), rs.getString("director")));
//            }
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//
//
//        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);
//
//        ListView listView = findViewById(R.id.list);
//        listView.setAdapter(adapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Movie movie = movies.get(position);
//                String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getTitle(), movie.getYear());
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}