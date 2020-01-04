package com.sbc.childtracker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.sbc.childtracker.requests.CustomRequest;
import com.sbc.childtracker.requests.ResponseMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

  private EditText editText_name;
  private EditText editText_surname;
  private EditText editText_phone;
  private EditText editText_email;
  private EditText editText_password;
  private Button button_register;

  private static final String serverAddress = "http://192.168.1.23:3000";
  private static final String TAG = RegisterActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    init();
  }

  private void init() {
    editText_name = findViewById(R.id.nameText);
    editText_surname = findViewById(R.id.surnameText);
    editText_phone = findViewById(R.id.phoneText);
    editText_email = findViewById(R.id.emailText);
    editText_password = findViewById(R.id.passwordText);

    button_register = findViewById(R.id.registerButton);

    button_register.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        makeRegisterRequest();
      }
    });
  }

  private void makeRegisterRequest() {
    String url = serverAddress + "/api/register";

    Map<String, String> params = new HashMap<>();

    params.put("name", editText_name.getText().toString());
    params.put("surname", editText_surname.getText().toString());
    params.put("phone", editText_phone.getText().toString());
    params.put("email", editText_email.getText().toString());
    params.put("password", editText_password.getText().toString());

    Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        Log.d(TAG, "Successful:\t" + response.toString());

        showSuccessMessage();
      }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Log.e(TAG, "Error:\t" + error.getMessage());

        showFailedMessage();
      }
    };

    CustomRequest customRequest = new CustomRequest(Request.Method.POST, url, params, successListener, errorListener);
    RequestQueue queue = Volley.newRequestQueue(this);

    queue.add(customRequest);
  }

  private void showSuccessMessage(){
    Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_LONG).show();
  }

  private void showFailedMessage(){
    Toast.makeText(getApplicationContext(), "Error while registering, please try again.", Toast.LENGTH_LONG).show();
  }
}
