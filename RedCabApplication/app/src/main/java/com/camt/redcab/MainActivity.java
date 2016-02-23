package com.camt.redcab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.camt.redcab.Maps.MapActivity;

public class MainActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private View loginBtn;
    private View signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindWidgets();
        loginEvent();
        signupEvent();
    }

    private void signupEvent() {
        signupBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this,RegistrationActivity.class));

                Intent i = new Intent(getApplicationContext(),RegistrationActivity.class);
                startActivity(i);
            }
        });
    }

    private void loginEvent() {

        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = edtUsername.getText().toString();
                final String password = edtPassword.getText().toString();

                //Toast toast = Toast.makeText(getApplicationContext(), username + " " + password, Toast.LENGTH_LONG);
                //toast.show();

                Intent i = new Intent(getApplicationContext(),MapActivity.class);
                startActivity(i);
            }
        });
    }

    private void bindWidgets() {
        edtUsername = (EditText) findViewById(R.id.usernameEditText);
        edtPassword = (EditText) findViewById(R.id.passwordEditText);
        loginBtn = findViewById(R.id.loginButton);
        signupBtn = findViewById(R.id.signupButton);
    }


}
