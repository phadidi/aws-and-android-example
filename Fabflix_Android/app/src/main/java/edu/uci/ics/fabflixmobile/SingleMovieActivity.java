package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class SingleMovieActivity extends Activity {
    private String url;
    private TextView mtitle;
    private TextView myear;
    private TextView mstars;
    private TextView mdirector;
    private TextView mgenres;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlemovie);
        //this should be retrieved from the database and the backend server

        url = "https://10.0.2.2:8443/cs122b-spring20-team-13/api/";

        mtitle = findViewById(R.id.movieTitle);
        myear = findViewById(R.id.movieYear);
        mstars = findViewById(R.id.movieStars);
        mdirector = findViewById(R.id.movieDirector);
        mgenres = findViewById(R.id.movieGenres);

        String movieId = getIntent().getStringExtra("id");

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest listRequest = new StringRequest(Request.Method.GET, url + "single-movie?id=" + movieId, response -> {
            Log.d("singleMovie.success", response);
            //System.out.println(response.getClass().getName());

            try {
                JSONArray jsonArray = new JSONArray(response);

                final JSONObject m = jsonArray.getJSONObject(0); // 0 because there's only 1 item
                String id = m.getString("id");
                String title = m.getString("title");
                String year = m.getString("year");
                String director = m.getString("director");
                System.out.println(director);
                String genre = m.getString("genres");
                System.out.println(genre);
                String stars = m.getString("stars");

                // parsing stars to remove ids
                String names = "";
                String[] starsSplit = stars.split(",");
                for(int i = 0; i < starsSplit.length; i++){
                    String[] star = starsSplit[i].split("_");
                    names += star[0] + ", ";
                }
                stars = names.substring(0, names.length() - 2);
                System.out.println(stars);

                mtitle.setText(title);
                myear.setText("Year: " + year);
                mdirector.setText("Director: " + director);
                mstars.setText("Starring: " + stars);
                mgenres.setText("Genre: " + genre);
            }
            catch(final JSONException e){
                Log.e("Json parsing error", e.toString());
            }
        },
                error -> {
                    // error
                    Log.d("singleMovie.error", error.toString());
                });

        queue.add(listRequest);
    }
}