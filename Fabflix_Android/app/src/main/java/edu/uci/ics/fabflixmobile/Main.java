package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

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