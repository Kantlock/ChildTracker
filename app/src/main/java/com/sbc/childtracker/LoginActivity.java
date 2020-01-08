package com.sbc.childtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sbc.childtracker.requests.CustomRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LoginActivity extends AppCompatActivity {

  private EditText editText_username;
  private EditText editText_password;
  private TextView textView_register;
  private Button button_send;

  private static final String TAG = LoginActivity.class.getSimpleName();
  private static final String serverAddress = "http://192.168.1.23:3000";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    checkWithAuthToken();

    init();
  }

  private void init() {
    editText_username = findViewById(R.id.usernameText);
    editText_password = findViewById(R.id.passwordText);
    textView_register = findViewById(R.id.lnkRegister);
    button_send = findViewById(R.id.sendButton);

    button_send.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                makeLoginRequest();
              }
            });

    textView_register.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
      }
    });
  }

  private void makeLoginRequest() {
    String url = serverAddress + "/api/login";

    Map<String, String> params = new HashMap<>();

    params.put("username", editText_username.getText().toString());
    params.put("password", editText_password.getText().toString());

    Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        Log.d(TAG, "Succesfull: " + response.toString());

        if (!response.toString().contains("Auth token present")) {
          SharedPreferences pref =
                  getApplicationContext().getSharedPreferences("MyPref", 0);
          Editor editor = pref.edit();

          editor.putString(
                  "auth",
                  response.toString().substring(
                          response.toString().indexOf("auth") + 7, response.toString().lastIndexOf('}') - 1));
        }

      }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Log.e(TAG, "Error: " + error.getMessage());
      }
    };

    CustomRequest customRequest = new CustomRequest(Request.Method.POST, url, params, successListener, errorListener);
    RequestQueue queue = Volley.newRequestQueue(this);

    // Add the request to the RequestQueue.
    queue.add(customRequest);
  }

  private void checkWithAuthToken() { // TODO CHANGE REQUEST IMPLEMENTATION TO CUSTOMREQUEST
    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);

    Optional<String> authToken = Optional.ofNullable(pref.getString("auth", null));

    authToken.ifPresent(
            token -> {
              RequestQueue queue = Volley.newRequestQueue(this);
              String url = serverAddress + "/api/login";

              StringRequest stringRequest =
                      new StringRequest(
                              Request.Method.POST,
                              url,
                              new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                  if (response.contains("Success")) {
                                    Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                                    startActivity(intent);
                                  }
                                }
                              },
                              new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                  Log.e(TAG, "Error: " + error.getMessage());

                                  Toast.makeText(getApplicationContext(), "Error while login\nPlease try again later", Toast.LENGTH_LONG).show();
                                }
                              }) {
                        @Override
                        public Map<String, String> getParams() {
                          Map<String, String> params = new HashMap<>();

                          params.put("auth", authToken.get());

                          return params; // return the parameters
                        }
                      };

              queue.add(stringRequest);
            });
  }
}
