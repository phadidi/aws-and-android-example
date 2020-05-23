package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

public class Main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //this should be retrieved from the database and the backend server


        final StringRequest mainRequest = new StringRequest(Request.Method.GET, "https://10.0.2.2:8443/cs122b-spring20-team-13/api/main", response -> {

        },
                error -> {
                    // error
                    Log.d("main.error", error.toString());
                });

    }
}