package com.sbc.childtracker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class RegisterActivity extends AppCompatActivity {

  private EditText editText_name;
  private EditText editText_surname;
  private EditText editText_phone;
  private EditText editText_email;
  private EditText editText_password;
  private Button button_register;

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
        User user = new User(editText_name.getText().toString(),
                editText_surname.getText().toString(),
                editText_phone.getText().toString(),
                editText_email.getText().toString(),
                editText_password.getText().toString());

        //TODO: SEND USER INFORMATION TO SERVER
      }
    });
  }

  @Getter
  @Setter
  @AllArgsConstructor
  private class User {
    private String name;
    private String surname;
    private String phone;
    private String email;
    private String password;

    @Override
    public String toString() {
      return name + " / " + surname + " / " + phone + " / " + email + " / " + password;
    }
  }
}
