package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

public class ListViewActivity extends Activity {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        //this should be retrieved from the database and the backend server
        final ArrayList<Movie> movies = new ArrayList<>();

        try {
            //TODO: correctly implement a mysql connection for future movielist queries
            Connection dbcon = DriverManager.getConnection("jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false", "mytestuser", "mypassword");
            PreparedStatement statementId = dbcon.prepareStatement("select * from movies limit 5;");
            ResultSet rs = statementId.executeQuery();
            while (rs.next()) {
                movies.add(
                        new Movie(rs.getString("id"), rs.getString("title"),
                                rs.getInt("year"), rs.getString("director")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


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
    }
}