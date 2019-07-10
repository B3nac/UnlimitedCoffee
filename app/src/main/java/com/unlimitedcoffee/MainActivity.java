package com.unlimitedcoffee;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    SessionPreferences session;
    ArrayList<String> smsMessagesList = new ArrayList<>();
    ListView messages;
    ArrayAdapter arrayAdapter;
    EditText input;
    EditText text_Phone_Number;
    String actualPhoneNumber;
    SmsManager smsManager = SmsManager.getDefault();
    PNDatabaseHelper PNdatabase;    // phone number data base helper

    private static MainActivity inst;

    private static final int SEND_SMS_PERMISSIONS_REQUEST = 1;

    public static MainActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Creating instance of Session preferences to store/check user login status
        session = new SessionPreferences(getApplicationContext());
        session.checkLogin();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messages = (ListView) findViewById(R.id.messages);
        input = (EditText) findViewById(R.id.input);
        text_Phone_Number = (EditText) findViewById(R.id.txt_phone_number);
        actualPhoneNumber = text_Phone_Number.getText().toString().trim();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        messages.setAdapter(arrayAdapter);
        PNdatabase = new PNDatabaseHelper(this);

        if (getIntent().hasExtra("com.unlimitedcoffee.SELECTED_NUMBER")) {
            String incomingNumber = getIntent().getStringExtra("com.unlimitedcoffee.SELECTED_NUMBER");
            actualPhoneNumber = incomingNumber;
            text_Phone_Number.setText(phoneNumberAlias(incomingNumber));
            text_Phone_Number.setEnabled(false);
        } else {
            text_Phone_Number.setText("");
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToSendSMS();
        } else {
            refreshSmsInbox();
        }

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
                Intent settings = new Intent(MainActivity.this, AccountSettingsActivity.class);
                startActivity(settings);
                return true;
            case R.id.contacts_search:
                Intent contacts_search = new Intent(MainActivity.this, ContactsSearchActivity.class);
                startActivity(contacts_search);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateInbox(final String smsMessage) {

        arrayAdapter.insert(smsMessage, 0);
        arrayAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onSendClick(View view) {
        String sentPhoneNumber = actualPhoneNumber;
        if (input.getText().toString().isEmpty()){
            Toast.makeText(this, "Enter a message.", Toast.LENGTH_SHORT).show();
        } else {
            if ((sentPhoneNumber.isEmpty())) {   // check for valid phone number entry
                Toast.makeText(this, "Enter a valid Phone Number", Toast.LENGTH_SHORT).show();
            } else {
                String textMessage = input.getText().toString();
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSIONS_REQUEST);
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    getPermissionToSendSMS();
                } else {
                    String encryptedText = TextEncryption.encrypt(textMessage);
                    smsManager.sendTextMessage(sentPhoneNumber, null, encryptedText, null, null);
                    String decryptedText = TextEncryption.decrypt(encryptedText);
                    System.out.println(decryptedText);
                    if (!PNdatabase.containsPhoneNumber(sentPhoneNumber.replace("+", ""))) {
                        PNdatabase.addPhoneNumber(sentPhoneNumber.replace("+", "")); // add phone number to the database
                    }
                    Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
                    input.setText("");
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToSendSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_SMS)) {
                Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, SEND_SMS_PERMISSIONS_REQUEST);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == SEND_SMS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Send SMS permission granted", Toast.LENGTH_SHORT).show();
                refreshSmsInbox();
            } else {
                Toast.makeText(this, "Send SMS permission denied", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    public void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        String[] requestedColumns = new String[]{"_id", "address", "body", "type"};
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms"), requestedColumns,
                null , null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        int indexType = smsInboxCursor.getColumnIndex("type");// 2 = sent, etc.)
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        do {
            if (smsInboxCursor.getString(indexAddress).equals(actualPhoneNumber)) {
                if (smsInboxCursor.getString(indexType).equals("1")) {

                    String str = "SMS From: " + phoneNumberAlias(smsInboxCursor.getString(indexAddress)) +
                            "\n" + TextEncryption.decrypt(smsInboxCursor.getString(indexBody)) + "\n";
                    arrayAdapter.add(str);
                }

                if (smsInboxCursor.getString(indexType).equals("2")) {
                    String str = "SMS To: " + phoneNumberAlias(smsInboxCursor.getString(indexAddress)) +
                            "\n" + TextEncryption.decrypt(smsInboxCursor.getString(indexBody)) + "\n";
                    arrayAdapter.add(str);
                }

            }
        } while (smsInboxCursor.moveToNext());
        //messages.setSelection(arrayAdapter.getCount() - 1);
    }

    public String getContactDisplayNameByNumber(String number, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "";

        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, null, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name += contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            }else{
                name = "";
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }

    public String phoneNumberAlias(String phoneNumber){
        if (getContactDisplayNameByNumber(phoneNumber, this).length() == 0){
            return phoneNumber;
        } else {
            return getContactDisplayNameByNumber(phoneNumber, this);
        }
    }

}
