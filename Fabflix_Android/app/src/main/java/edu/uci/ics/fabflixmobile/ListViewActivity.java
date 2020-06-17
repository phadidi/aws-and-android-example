package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.RequiresApi;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends Activity {
    private String url;
    private Button next;
    private Button previous;
    private String query;
    private int page;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);

        final ArrayList<Movie> movies = new ArrayList<>();

        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);

        url = "https://10.0.2.2:8443/cs122b-spring20-team-13/api/";

        next.setOnClickListener(view -> Toast.makeText(getApplicationContext(), "There are no more pages", Toast.LENGTH_SHORT).show());
        previous.setOnClickListener(view -> Toast.makeText(getApplicationContext(), "There are no previous pages", Toast.LENGTH_SHORT).show());

        page = getIntent().getIntExtra("page", 1);

        if (page != 1) {
            previous.setOnClickListener(view -> previous());
        }

        query = getIntent().getStringExtra("query");
        query = String.join("%20", query.split(" "));

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest listRequest = new StringRequest(Request.Method.GET, url + "movielist?page=" + Integer.toString(page) + "&sort=title_asc_rating_asc&limit=20&search_title=" + query, response -> {

            Log.d("list.success", response);
            System.out.println(page);

            try {
                JSONArray jsonArray = new JSONArray(response);

                if (jsonArray.length() == 20) {
                    next.setOnClickListener(view -> next());
                }

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
                System.out.println(movies);
                MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);
                System.out.println("CREATE ADAPTER");

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
                Log.d("Json parsed", response);
            } catch (final JSONException e) {
                Log.e("Json parsing error", e.toString());
            }
        },
                error -> {
                    // error
                    Log.d("list.error", error.toString());
                });

        queue.add(listRequest);
//        movies.add(new Movie("123","TITLE", 1998, "DIRECTOR", "test", "DUY"));
        //queue.add(listRequest);
    }

    public void next() {
        Intent nextView = new Intent(ListViewActivity.this, ListViewActivity.class);
        nextView.putExtra("query", query);
        page += 1;
        nextView.putExtra("page", page);

        startActivity(nextView);
    }

    public void previous() {
        Intent previousView = new Intent(ListViewActivity.this, ListViewActivity.class);
        previousView.putExtra("query", query);
        page -= 1;
        previousView.putExtra("page", page);

        startActivity(previousView);
    }
}