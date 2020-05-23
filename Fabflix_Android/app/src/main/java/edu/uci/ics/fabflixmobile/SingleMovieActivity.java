package edu.uci.ics.fabflixmobile;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class SingleMovieActivity extends AppCompatActivity {
    private String url;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlemovie);
        //TODO: pass Movie object into here
        TextView titleView = findViewById(R.id.movieTitle);
        //titleView.setText(movie.getTitle());
        TextView starsView = findViewById(R.id.movieStars);
        //starsView.setText("Stars: " + movie.getStars());
        TextView yearView = findViewById(R.id.movieYear);
        //yearView.setText("Year: " + movie.getYear());
        TextView directorView = findViewById(R.id.movieDirector);
        //directorView.setText("Director: " + movie.getYear());
        TextView genresView = findViewById(R.id.movieGenres);
        //genresView.setText("Genres: " + movie.getGenre());

    }

}
