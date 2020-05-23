package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListViewActivity extends Activity {
    private String url;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        //this should be retrieved from the database and the backend server

        final ArrayList<Movie> movies = new ArrayList<>();

        url = "https://10.0.2.2:8443/cs122b-spring20-team-13/api/";

        String query = getIntent().getStringExtra("query");
        query = String.join("%20", query.split(" "));

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest listRequest = new StringRequest(Request.Method.GET, url + "movielist?page=1&sort=title_asc_rating_asc&limit=10&search_title=" + query, response -> {
            //TODO should parse the json response to redirect to appropriate functions.
            Log.d("list.success", response);
            //System.out.println(response.getClass().getName());

            try {
                JSONArray jsonArray = new JSONArray(response);

                // Getting JSON Array node
                for (int i = 0; i < jsonArray.length(); i++) {
                    final JSONObject m = jsonArray.getJSONObject(i);
                    String id = m.getString("id");
                    String title = m.getString("title");
                    String year = m.getString("year");
                    String director = m.getString("director");
                    String genre = m.getString("genres");
                    String stars = m.getString("starNamesAndIds");
                    movies.add(new Movie(id, title, Integer.parseInt(year), director, genre, stars));
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


        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);

                Intent singleMovie = new Intent(ListViewActivity.this, SingleMovieActivity.class);
                singleMovie.putExtra("id", movie.getId());
                // without starting the activity/page, nothing would happen
                String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getTitle(), movie.getYear());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                startActivity(singleMovie);
            }
        });

        queue.add(listRequest);
    }
}