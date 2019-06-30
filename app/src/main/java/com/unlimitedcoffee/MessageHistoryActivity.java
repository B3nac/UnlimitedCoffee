package com.unlimitedcoffee;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MessageHistoryActivity extends AppCompatActivity {

    SessionPreferences session;
    FloatingActionButton newMsgBtn;

    /**
     * One create method for the message History activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Creating instance of Session preferences to store/check user login status
        session = new SessionPreferences(getApplicationContext());
        session.checkLogin();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_history);
        // assign new message button
        newMsgBtn = (FloatingActionButton) findViewById(R.id.newMsgBtn);

        //send user to new message entry page
        newMsgBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent toNewMessage = new Intent(MessageHistoryActivity.this, MainActivity.class);
            startActivity(toNewMessage);
        }
        }); // end newMsgBtn onClick

    } // end onCreate

    /**
     * The following two methods create the menu of options in MainActivity
     * @param menu
     * @return true (boolean)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu2, menu);
        return true;
    }   // end onCreateOptionMenu

    /**
     * This method implemented the menu options on the message history activity
     *
     * @param item
     * @return a boolean value to validate the selection.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, "Selected Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.newMsgBtn_settings:   // New message
                Intent toNewMessage = new Intent(MessageHistoryActivity.this, MainActivity.class);
                startActivity(toNewMessage);
                return true;
            case R.id.newGrpMsgBtn_settings:    // new Group Message
                Intent toNewGrpMessage = new Intent(MessageHistoryActivity.this, MainActivity.class);
                startActivity(toNewGrpMessage);
                return true;
            case R.id.logout:   // logout
                session.logoutUser();
                return true;
            case R.id.app_settings: // application settings
                Intent settings = new Intent(MessageHistoryActivity.this, AccountSettingsActivity.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }   // end switch
    }   // end menu option selection
}   // end class

