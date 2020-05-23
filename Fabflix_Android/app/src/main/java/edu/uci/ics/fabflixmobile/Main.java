package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main extends Activity {
    private String url;
    private EditText query;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        query = findViewById(R.id.search_bar);
        searchButton = findViewById(R.id.search_button);

        url = "https://10.0.2.2:8443/cs122b-spring20-team-13/api/";

        searchButton.setOnClickListener(view -> search());
    }

    public void search() {

        if (query.getText().toString().matches("")) {
            Toast.makeText(this, "Please enter your search query", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Fetching search results", Toast.LENGTH_SHORT).show();
            final RequestQueue queue = NetworkManager.sharedManager(this).queue;

            final StringRequest mainRequest = new StringRequest(Request.Method.GET, url + "main", response -> {
                //TODO should parse the json response to redirect to appropriate functions.
                Log.d("main.success", response);
                // initialize the activity(page)/destination
                Intent listView = new Intent(Main.this, ListViewActivity.class);
                listView.putExtra("query", query.getText().toString());
                // without starting the activity/page, nothing would happen
                startActivity(listView);
            },
                    error -> {
                        // error
                        Log.d("main.error", error.toString());
                    });
            queue.add(mainRequest);
        }
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