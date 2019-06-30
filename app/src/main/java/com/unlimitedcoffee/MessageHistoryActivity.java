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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Creating instance of Session preferences to store/check user login status
        session = new SessionPreferences(getApplicationContext());
        session.checkLogin();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_history);
        // assign new message button
        newMsgBtn  = (FloatingActionButton) findViewById(R.id.newMsgBtn);

        newMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send user to new m1301essage entry page
                Intent toNewMessage = new Intent(MessageHistoryActivity.this, MainActivity.class);
                startActivity(toNewMessage);
            }
        });

    }

    /*
    The following two methods create the menu of options in MainActivity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.logout:
                session.logoutUser();
                return true;
            case R.id.app_settings:
                Intent settings = new Intent(MessageHistoryActivity.this, AccountSettingsActivity.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
