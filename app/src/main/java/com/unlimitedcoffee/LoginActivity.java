package com.unlimitedcoffee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    SessionPreferences session;
    EditText mTextPhoneNumber;
    EditText mTextPassword;
    Button mLoginButton;
    TextView mTextViewRegister;
    DatabaseHelper db;

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Instance of session to establish log in status
        session = new SessionPreferences(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Instance of db helper
        db = new DatabaseHelper(this);

        mTextPhoneNumber = (EditText) findViewById(R.id.edittext_username);
        mTextPassword = (EditText) findViewById(R.id.edittext_password);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mTextViewRegister = (TextView) findViewById(R.id.textview_register);

        //OnClickListener for register text
        mTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        //OnClickListener for Login button
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = Utilities.sanitize(mTextPhoneNumber.getText().toString());
                String password = Utilities.sanitize(mTextPassword.getText().toString());
                Boolean res = db.checkUser(phoneNumber,password);
                //If phone number and password are correct
                if(res == true) {
                    session.createLoginSession(phoneNumber);
                    //send the user to the message history page
                    Intent messageHistApp = new Intent(LoginActivity.this, MessageHistoryActivity.class);
                    startActivity(messageHistApp);

                }else{
                    Toast.makeText(LoginActivity.this, "Wrong password/phone number",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
