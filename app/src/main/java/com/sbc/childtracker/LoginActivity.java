package com.sbc.childtracker;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LoginActivity extends AppCompatActivity {

  private EditText editText_username;
  private EditText editText_password;
  private Button button_send;

  private static final String TAG = LoginActivity.class.getSimpleName();
  private static final String serverAddress = "http://192.168.1.23:3000";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

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
                        Log.e(TAG, "Success, switching to google maps activity");
                      }
                    }
                  },
                  new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                      Log.e(TAG, "Error: " + error.getMessage());
                    }
                  }) {
                @Override
                public Map<String, String> getParams() {
                  Map<String, String> params = new HashMap<>();

                  SharedPreferences pref =
                      getApplicationContext().getSharedPreferences("MyPref", 0);

                  params.put("auth", authToken.get());

                  return params; // return the parameters
                }
              };

          queue.add(stringRequest);
        });

    init();
  }

  private void init() {
    editText_username = findViewById(R.id.usernameText);
    editText_password = findViewById(R.id.passwordText);
    button_send = findViewById(R.id.sendButton);

    button_send.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            test();
          }
        });
  }

  private void test() {
    RequestQueue queue = Volley.newRequestQueue(this);
    String url = serverAddress + "/api/login";

    StringRequest stringRequest =
        new StringRequest(
            Request.Method.POST,
            url,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                Log.e(TAG, "Succesfull: " + response);

                if (!response.contains("Auth token present")) {
                  SharedPreferences pref =
                      getApplicationContext().getSharedPreferences("MyPref", 0);
                  Editor editor = pref.edit();

                  editor.putString(
                      "auth",
                      response.substring(
                          response.indexOf("auth") + 7, response.lastIndexOf('}') - 1));
                }
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
              }
            }) {
          @Override
          public Map<String, String> getParams() {
            Map<String, String> params = new HashMap<>();

            params.put("username", editText_username.getText().toString());
            params.put("password", editText_password.getText().toString());

            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);

            Optional<String> authToken = Optional.ofNullable(pref.getString("auth", null));

            authToken.ifPresent(token -> params.put("auth", token));

            return params; // return the parameters
          }
        };

    // Add the request to the RequestQueue.
    queue.add(stringRequest);
  }
}
