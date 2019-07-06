package com.unlimitedcoffee;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioFocusRequest;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MessageHistoryActivity extends AppCompatActivity {

    SessionPreferences session;
    FloatingActionButton newMsgBtn;
    ListView smsListView;
    MessageHistAdapter msgAdapter;
    ArrayList<String> phoneNumber = new ArrayList<>();
    ArrayList<String> messages = new ArrayList<>();
    ArrayList <Conversation> conversations = new ArrayList<Conversation>();

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

        smsListView = (ListView) findViewById(R.id.lvMsg);
        registerForContextMenu(smsListView);

        conversations = generateConversationHistory();
        msgAdapter = new MessageHistAdapter (this, phoneNumber , messages);
        smsListView.setAdapter(msgAdapter);

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

        smsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
                Intent toMessageThread = new Intent(MessageHistoryActivity.this, MainActivity.class);
                startActivity(toMessageThread);
            }
        });

    } // end onCreate

    /**
     * The following creates a context menu after long press
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.lvMsg) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu_hist, menu);
        }



    }

    /**
     * The following method creates the context menu options during long press
     * @param item
     * @returns a boolean confirmation
     */

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.Open_menu:
                Intent toNewMessage = new Intent(MessageHistoryActivity.this, MainActivity.class);
                startActivity(toNewMessage);
                return true;
            case R.id.Delete_menu:
                // remove stuff here
                AdapterView.AdapterContextMenuInfo info =
                        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Toast.makeText(this, "Selected Item: " +
                        info.id, Toast.LENGTH_SHORT).show();
                final int index = (int) info.id;
                AlertDialog.Builder alert = new AlertDialog.Builder(MessageHistoryActivity.this);
                alert.setMessage(" Are you sure you want to delete this message from this App?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        conversations.remove(index);
                        onResume();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.create();
                alert.show();

                onResume();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    /**
     * The following two methods create the menu of options in MessageHistoryActivity
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
            case R.id.DeleteAll_settings:    // new Group Message
                deleteAllMessages();
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

    /**
     * This method refreshes the list display
     */
    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        refreshSMSInbox();
        msgAdapter = new MessageHistAdapter (this, phoneNumber , messages);
        smsListView.setAdapter(msgAdapter);
    }

    /**
     * This method generates the conversation history of messages on the phone
     * @return ArrayList of Conversation objects
     */

    private ArrayList <Conversation> generateConversationHistory(){
        ArrayList <String> StoredPhoneNumbers = new ArrayList<String>();
        ArrayList <Message> StoredMessages = new ArrayList<Message>();

        //pooling text messages from the sms manager
        Uri inboxURI = Uri.parse("content://sms");
        String[] requestedColumns = new String[]{"_id", "address", "body"};
        ContentResolver cr = getContentResolver();
        Cursor smsInboxCursor = cr.query(inboxURI, requestedColumns, null, null,null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");

        smsInboxCursor.moveToFirst(); // last text sent

        // The next few lines are to group messages per phone number
        if (indexBody < 0 ||  !smsInboxCursor.moveToNext()) return null;
        smsInboxCursor.moveToFirst();
        do {

            if (!StoredPhoneNumbers.contains(smsInboxCursor.getString(indexAddress))){
                StoredPhoneNumbers.add(smsInboxCursor.getString(indexAddress)); // add phone number
            }
            StoredMessages.add(new Message(smsInboxCursor.getString(indexAddress),
                    smsInboxCursor.getString(indexBody)));    // add message

        } while(smsInboxCursor.moveToNext());

        for (String sNumber: StoredPhoneNumbers) {   // group messages per phone number]

            ArrayList<String> numMessages = new ArrayList<String>();
            for (Message m : StoredMessages) {
                if (m.getNumber().equals(sNumber)) {
                    numMessages.add(m.getBody());
                }
            }
            conversations.add(new Conversation(sNumber, numMessages)); // add new conversation
        }
        return conversations;
    }

    /**
     * This method refreshes the content of the inbox
     *
     */
    private void refreshSMSInbox() {
        phoneNumber.clear();
        messages.clear();


        for (Conversation c: conversations) {   // this returns the last phone number and conversation
            phoneNumber.add(c.getNumber());
            messages.add(c.findLastMessage());
        }
    }

    private void deleteAllMessages(){
        AlertDialog.Builder alert = new AlertDialog.Builder(MessageHistoryActivity.this);
        alert.setMessage(" Are you sure you want to delete all message from this App?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                conversations.clear();
                onResume();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.create();
        alert.show();
    }

}   // end class

