package com.unlimitedcoffee;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
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
<<<<<<< HEAD
    private ListView smsListView;
    private MessageHistAdapter msgAdapter;
    private ArrayList<String> phoneNumber = new ArrayList<>();
    private ArrayList<String> messages = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<String> readStat = new ArrayList<>();
    private ArrayList <Conversation> conversations = new ArrayList<Conversation>();
    PNDatabaseHelper PNdatabase;
    private static final int REQUEST_PERMISSION = 1;
=======
    ListView smsListView;
    MessageHistAdapter msgAdapter;
    ArrayList<String> phoneNumber = new ArrayList<>();
    ArrayList<String> messages = new ArrayList<>();
    ArrayList <Conversation> conversations = new ArrayList<Conversation>();

    @Override // prevents navigation back to Login or Registration pages
    public void onBackPressed() {
        moveTaskToBack(true);
    }

>>>>>>> a7a42df4403bb51ff86f28a56b7a878707ba5dd4
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

        msgAdapter = new MessageHistAdapter (this, phoneNumber , messages, dates, readStat);
        smsListView.setAdapter(msgAdapter);

        PNdatabase = new PNDatabaseHelper(this);
        checkDatabase();
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
                String selectedMsg = conversations.get(position).getNumber();
                toMessageThread.putExtra("com.unlimitedcoffee.SELECTED_NUMBER", selectedMsg);
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
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();    // get ID from adapter
        final int index = (int) info.id;
        switch(item.getItemId()) {
            case R.id.Open_menu:
                Intent toNewMessage = new Intent(MessageHistoryActivity.this, MainActivity.class);
                String selectedMsg = conversations.get(index).getNumber();
                toNewMessage.putExtra("com.unlimitedcoffee.SELECTED_NUMBER", selectedMsg);
                startActivity(toNewMessage);
                return true;
            case R.id.Delete_menu:

                deleteMessageThread(index); // delete thread with provided id
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
            case R.id.refreshBtn:
                refreshAllMessages();
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
     * This method refreshes the list display after a pause
     */
    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        msgAdapter = new MessageHistAdapter (this, phoneNumber , messages, dates, readStat);
        smsListView.setAdapter(msgAdapter);
        refreshSMSInbox();

    }

    /**
     * This method refreshes the app after the back button is hit from another page
     */
    @Override
    public void onRestart(){
        super.onRestart();
        checkDatabase();
        refreshSMSInbox();
    }

    /**
     * This method generates the conversation history of messages on the phone
     * @return ArrayList of Conversation objects
     */

    private ArrayList <Conversation> generateConversationHistory(){
        ArrayList <Message> StoredMessages = new ArrayList<Message>();

        //pooling text messages from the sms manager
        Uri inboxURI = Uri.parse("content://sms");
        String[] requestedColumns = new String[]{"_id", "address", "body", "type", "date", "read"};
        ContentResolver cr = getContentResolver();
        Cursor smsInboxCursor = cr.query(inboxURI, requestedColumns, null, null,null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        int indexType = smsInboxCursor.getColumnIndex("type");// 1 = received, 2 = sent, etc.)
        int indexDate = smsInboxCursor.getColumnIndex("date");
        int indexRead = smsInboxCursor.getColumnIndex("read");
        smsInboxCursor.moveToFirst(); // last text sent

        // The next few lines are to group messages per phone number
        if (indexBody < 0 || !smsInboxCursor.moveToNext()) return null;
        smsInboxCursor.moveToFirst();
        do {
            if (!PNdatabase.containsPhoneNumber(smsInboxCursor.getString(indexAddress))){
                PNdatabase.addPhoneNumber(smsInboxCursor.getString(indexAddress)); // add phone number
            }
            if (smsInboxCursor.getString(indexType).equals("1")) {  // received messages
                StoredMessages.add(new Message(smsInboxCursor.getString(indexAddress),
                        TextEncryption.decrypt(smsInboxCursor.getString(indexBody)),
                        smsInboxCursor.getString(indexDate),
                        smsInboxCursor.getString(indexRead)));    // add time Stamp
            }
            if (smsInboxCursor.getString(indexType).equals("2")) {  // sent messages
                StoredMessages.add(new Message(smsInboxCursor.getString(indexAddress),
                        "You: " + TextEncryption.decrypt(smsInboxCursor.getString(indexBody)),
                        smsInboxCursor.getString(indexDate),
                        smsInboxCursor.getString(indexRead)));    // add time Stamp
            }

        } while(smsInboxCursor.moveToNext());

        Cursor PNnumbers = PNdatabase.getAllPhoneNumber();
        PNnumbers.moveToFirst();
        int index = PNnumbers.getColumnIndex("phone_numbers");
        do {   // group messages per phone number]
            ArrayList<String> numMessages = new ArrayList<String>();
            ArrayList<String> numTimes = new ArrayList<String>();
            ArrayList<String> numRdStat = new ArrayList<>();
            for (Message m : StoredMessages) {
                if (m.getNumber().equals("+" + PNnumbers.getString(index))) {
                    numMessages.add(m.getBody());
                    numTimes.add(m.getTimeStamp());
                    numRdStat.add(m.getReadStat());
                }
            }
            conversations.add(new Conversation(("+" + PNnumbers.getString(index)), numMessages,
                    numTimes, numRdStat)); // add new conversation
        } while (PNnumbers.moveToNext());
        return conversations;
    }

    /**
     * This method refreshes the content of the inbox
     *
     */
    private void refreshSMSInbox() {
        phoneNumber.clear();
        messages.clear();
        readStat.clear();
        for (Conversation c: conversations) {   // this returns the last phone number and conversation
            readStat.add(c.findLastReadStat());
            messages.add(c.findLastMessage());
            dates.add(c.findLastTimeStamp());
            phoneNumber.add(phoneNumberAlias(c.getNumber()));
        }
    }


    /**
     * This method deletes all message threads in the conversation history
     */

    private void deleteAllMessages(){
        AlertDialog.Builder alert = new AlertDialog.Builder(MessageHistoryActivity.this);
        alert.setMessage(" Are you sure you want to delete all message from this App?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PNdatabase.deleteAllPhoneNumbers();
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

    /**
     * This message removes the conversation record at the index provided
     * @param threadID
     */
    private void deleteMessageThread(final int threadID){
        AlertDialog.Builder alert = new AlertDialog.Builder(MessageHistoryActivity.this);
        alert.setMessage(" Are you sure you want to delete this message from this App?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PNdatabase.deletePhoneNumber(conversations.get(threadID).getNumber().replace("+", ""));
                conversations.remove(threadID);
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

    /**
     * This method reloads all messages in the messaging inbox back to the app
     */
    private void refreshAllMessages(){
        AlertDialog.Builder alert = new AlertDialog.Builder(MessageHistoryActivity.this);
        alert.setMessage(" Are you sure you want to reload all message to this App?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                conversations.clear();
                conversations = generateConversationHistory();
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

    /**
     * This method checks if there was an update to the native SMS app
     * and asks to load those messages
     */
    public void checkDatabase(){
        if (PNdatabase.isEmpty()){
            AlertDialog.Builder alert = new AlertDialog.Builder(MessageHistoryActivity.this);
            alert.setMessage(" Do you want to load all message to this App?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    conversations.clear();
                    conversations = generateConversationHistory();
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
        } else {
            conversations.clear();
            conversations = generatePNBasedHistory();
            onResume();
        }
    }

    /**
     * This method generates the history view based on phone numbers/conversation that were
     * not previously deleted
     * @return conversation
     */
    private ArrayList <Conversation> generatePNBasedHistory(){
        ArrayList <Message> StoredMessages = new ArrayList<Message>();
        ArrayList <String> StoredNumbers = new ArrayList<String>();
        //pooling text messages from the sms manager
        Uri inboxURI = Uri.parse("content://sms");
        String[] requestedColumns = new String[]{"_id", "address", "body", "type", "date", "read"};
        ContentResolver cr = getContentResolver();
        Cursor smsInboxCursor = cr.query(inboxURI, requestedColumns, null, null,null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        int indexType = smsInboxCursor.getColumnIndex("type");// 2 = sent, etc.)
        int indexDate = smsInboxCursor.getColumnIndex("date");
        int indexRead = smsInboxCursor.getColumnIndex("read");
        smsInboxCursor.moveToFirst(); // last text sent

        // The next few lines are to group messages per phone number
        if (indexBody < 0 || !smsInboxCursor.moveToNext()) return null;
        smsInboxCursor.moveToFirst();
        do {

            if (!StoredNumbers.contains(smsInboxCursor.getString(indexAddress))){
                StoredNumbers.add(smsInboxCursor.getString(indexAddress)); // add phone number
            }
            if (smsInboxCursor.getString(indexType).equals("1")) {
                StoredMessages.add(new Message(smsInboxCursor.getString(indexAddress),
                        TextEncryption.decrypt(smsInboxCursor.getString(indexBody)),
                        smsInboxCursor.getString(indexDate),
                        smsInboxCursor.getString(indexRead)));    //add time Stamp
            }
            if (smsInboxCursor.getString(indexType).equals("2")) {
                StoredMessages.add(new Message(smsInboxCursor.getString(indexAddress),
                        "You: " + TextEncryption.decrypt(smsInboxCursor.getString(indexBody)),
                        smsInboxCursor.getString(indexDate),
                        smsInboxCursor.getString(indexRead)));    // add time Stamp
            }

        } while(smsInboxCursor.moveToNext());
        //save conversation list based on teh phone numbers in the database
        for (String numMsg: StoredNumbers){
            ArrayList<String> numMessages = new ArrayList<String>();
            ArrayList<String> numTimes = new ArrayList<String>();
            ArrayList<String> numRdStat = new ArrayList<>();
            if  (PNdatabase.containsPhoneNumber(numMsg.replace("+", ""))) {
                for (Message m : StoredMessages) {
                    if (m.getNumber().equals(numMsg)) {
                        numMessages.add(m.getBody());
                        numTimes.add(m.getTimeStamp());
                        numRdStat.add(m.getReadStat());
                    }
                }
                conversations.add(new Conversation(numMsg, numMessages,
                        numTimes, numRdStat)); // add new conversation

            }
        }
        updateTable(StoredNumbers);
        return conversations;
    }

    /**
     * This method updates the database numbers with the new numbers in SMS
     * @param newTable
     */
    private void updateTable(ArrayList<String> newTable){
        Cursor tempCursor = PNdatabase.getAllPhoneNumber();
        int indexTemp = tempCursor.getColumnIndex("phone_numbers");
        tempCursor.moveToFirst();
        do {
            if (!newTable.contains("+" + tempCursor.getString(indexTemp))){
                PNdatabase.deletePhoneNumber(tempCursor.getString(indexTemp));
            }
        } while(tempCursor.moveToNext());
    }

    /**
     *This method looksup the phone number and translates it to the contact name
     * @param number
     * @param context
     * @return
     */

    public String getContactDisplayNameByNumber(String number, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "";
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_CONTACTS}, REQUEST_PERMISSION);

        }
        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, null, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name += contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }
        contactLookup.close();

        return name;
    }

    public String phoneNumberAlias(String phoneNumber){
        if (getContactDisplayNameByNumber(phoneNumber, this).length() == 0){
            return phoneNumber;
        } else {
            return getContactDisplayNameByNumber(phoneNumber, this);
        }
    }


    /*
    private void displayDatabase(){
        Cursor PNInboxCursor = PNdatabase.getAllPhoneNumber();
        int pnIndexAddress = PNInboxCursor.getColumnIndex("phone_numbers");

        if (pnIndexAddress < 0 ||  !PNInboxCursor.moveToNext()) {
            System.out.println("Table Empty");
            return;
        }
        PNInboxCursor.moveToFirst();
        do {
            System.out.println("Content : " + PNInboxCursor.getString(pnIndexAddress));
        } while (PNInboxCursor.moveToNext());
    }*/

}   // end class

