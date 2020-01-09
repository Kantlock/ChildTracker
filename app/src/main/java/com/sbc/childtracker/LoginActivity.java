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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LoginActivity extends AppCompatActivity {

  private EditText editText_email;
  private EditText editText_password;
  private TextView textView_register;
  private Button button_send;

  private static final String TAG = LoginActivity.class.getSimpleName();
  private static final String serverAddress = "http://192.168.1.22:3000";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    if (!getIntent().hasExtra("FromRegister")) checkWithAuthToken();

    init();
  }

  private void init() {
    editText_email = findViewById(R.id.emailText);
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

    textView_register.setOnClickListener(
        new View.OnClickListener() {
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

    params.put("email", editText_email.getText().toString());
    params.put("password", editText_password.getText().toString());

    Response.Listener<JSONObject> successListener =
        new Response.Listener<JSONObject>() {
          @Override
          public void onResponse(JSONObject response) {
            Log.d(TAG, "Succesfull: " + response.toString());

            if (!response.toString().contains("Auth token present")) {
              SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
              Editor editor = pref.edit();

              editor.putString(
                  "auth",
                  response
                      .toString()
                      .substring(
                          response.toString().indexOf("auth") + 7,
                          response.toString().lastIndexOf('}') - 1));

              editor.commit();
            }

            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
            startActivity(intent);
          }
        };

    Response.ErrorListener errorListener =
        new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "Error: " + new String(error.networkResponse.data, StandardCharsets.UTF_8));

            Toast.makeText(
                    getApplicationContext(),
                    "Error while login\nPlease try again later",
                    Toast.LENGTH_LONG)
                .show();
          }
        };

    CustomRequest customRequest =
        new CustomRequest(Request.Method.POST, url, params, successListener, errorListener);
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
                      Log.d(TAG, "Success with auth token login:\t" + response);

                      if (response.contains("Success")) {
                        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                        startActivity(intent);
                      }
                    }
                  },
                  new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                      Log.e(
                          TAG,
                          "Error: "
                              + new String(error.networkResponse.data, StandardCharsets.UTF_8));
                    }
                  }) {
                @Override
                public Map<String, String> getParams() {
                  Map<String, String> params = new HashMap<>();

                  params.put("auth", token);

                  return params; // return the parameters
                }
              };

          queue.add(stringRequest);
        });
  }
}
