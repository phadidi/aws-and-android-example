package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

//Converted ActionBarActivity to AppCompatActivity due to deprecation

public class Login extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private TextView message;
    private Button loginButton;
    private String url;
    //private String SITE_KEY = "6LeKvPAUAAAAABY_fGoGUY8FVwSafDXBozZJvk4I";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // upon creation, inflate and initialize the layout
        setContentView(R.layout.login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        message = findViewById(R.id.message);
        loginButton = findViewById(R.id.login);
        /**
         * In Android, localhost is the address of the device or the emulator.
         * To connect to your machine, you need to use the below IP address
         * **/
        url = "https://10.0.2.2:8443/cs122b-spring20-team-13/api/";

        //assign a listener to call a function to handle the user request when clicking a button
        loginButton.setOnClickListener(view -> login());
    }

    public void login() {
        message.setText("Trying to login");
        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest loginRequest = new StringRequest(Request.Method.POST, url + "login", response -> {
            //TODO should parse the json response to redirect to appropriate functions.
            Log.d("login.success", response);
            System.out.println(response);
            // initialize the activity(page)/destination
            Intent listPage = new Intent(Login.this, ListViewActivity.class);
            // without starting the activity/page, nothing would happen
            startActivity(listPage);
        },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Post request form data
                final Map<String, String> params = new HashMap<>();
                params.put("email", email.getText().toString());
                params.put("password", password.getText().toString());
                return params;
            }
        };
        // !important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }
}