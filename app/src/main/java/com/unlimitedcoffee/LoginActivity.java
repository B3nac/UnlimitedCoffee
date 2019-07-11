package com.unlimitedcoffee;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
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
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
        }

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
                String event;
                String time = Utilities.getTimeStr(); // gets current time as string

                // clear user entry fields
                mTextPhoneNumber.getText().clear();
                mTextPassword.getText().clear();

                // check lockout
                boolean lockStatus = db.checkAccountStatus(phoneNumber); //true = no lock on account, OK to proceed

                if (!lockStatus) { //false = locked account, back to the Login Page
                    Toast.makeText(LoginActivity.this, "Account Locked", Toast.LENGTH_SHORT).show();

                } else { // true = unlocked account, proceed with user authentication
                    boolean result = db.checkUser(phoneNumber,password); // validates phone / pword combo

                    if(result) { // credentials are valid
                        session.createLoginSession(phoneNumber);
                        // log user event to db: successful login
                        event = "successful login";
                        db.logEvent(phoneNumber, time, event);
                        Toast.makeText(LoginActivity.this, "Login successful - Welcome!",Toast.LENGTH_SHORT).show();

                        //send the user to the message history page
                        Intent messageHistApp = new Intent(LoginActivity.this, MessageHistoryActivity.class);
                        startActivity(messageHistApp);
                    }else{ // credentials are invalid
                        event = "failed login";
                        db.logEvent(phoneNumber, time, event);
                        Toast.makeText(LoginActivity.this, "Wrong password/phone number",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onSendClick(View view) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                getPermissionToReadSMS();
            } else {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            }
        }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_SMS)) {
                Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, READ_SMS_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_SMS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

}
