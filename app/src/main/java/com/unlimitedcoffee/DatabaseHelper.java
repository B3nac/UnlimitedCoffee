package com.unlimitedcoffee;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import org.mindrot.jbcrypt.BCrypt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final byte[] databaseBytes = Hide.getDatabaseName();
    public static final String databaseName = new String(databaseBytes);
    // vars for DB, User table
    public static final String DATABASE_NAME = databaseName;
    public static final int DATABASE_VERSION = 1;
    // vars User table
    public static final String TABLE_NAME = "user";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_USER_PHONE = "user_phone";
    public static final String COL_USER_PASSWORD = "user_password";
    // vars for Login table
    public static final String TABLE_NAME2 = "login";
    public static final String COL_LOG_ID = "ID";
    public static final String COL_LOG_PHONE = "phoneNum";
    public static final String COL_LOG_TIME = "time";
    public static final String COL_LOG_EVENT = "event";

    // create User Table
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_USER_PHONE + " STRING,"
            + COL_USER_PASSWORD + " TEXT)";
    //Drop User Table
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // create Login Table
    private String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_NAME2 + "("
            + COL_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_LOG_PHONE + " STRING,"
            + COL_LOG_TIME + " STRING," + COL_LOG_EVENT + " STRING)";
    //Drop Login Table
    private String DROP_LOGIN_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME2;

    //Class constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(CREATE_LOGIN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_USER_TABLE);
        sqLiteDatabase.execSQL(DROP_LOGIN_TABLE);
        onCreate(sqLiteDatabase);
    }

    //Method to add new users
    public void addUser(String phoneNumber, String password) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_USER_PHONE, phoneNumber);
        contentValues.put(COL_USER_PASSWORD, password);

        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }

    //Method to delete existing accounts
    public void deleteUser(String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by phone number
        db.delete(TABLE_NAME, COL_USER_PHONE + " = ?",
                new String[]{String.valueOf(phoneNumber)});
        db.close();
    }

    //Checks database for phone number and password
    public boolean checkUser(String phoneNumber, String password) {
        boolean isValid;
        String hashed_pword;

        // 1. pull saved password hash from db
        String[] columns = {COL_USER_PASSWORD};
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_USER_PHONE + "=?";
        String[] selectionArgs = {phoneNumber};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            hashed_pword = cursor.getString(0);
            // 2. use jBcrypt checkpw() function to verify password
            if (BCrypt.checkpw(password, hashed_pword)) {
                isValid = true; // pword matches
            } else {
                isValid = false; // pword does not match
            }
        } else {
            isValid = false;
        }
        cursor.close();
        db.close();
        return isValid;
    }

    //Checks if phone number is already in database
    public boolean checkPhone(String phoneNumber) {

        String[] columns = {COL_USER_ID};
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_USER_PHONE + "=?";
        String[] selectionArgs = {phoneNumber};

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        int count = cursor.getCount();

        db.close();

        if (count > 0)
            return true;
        else
            return false;
    }

    //Method to log event to db
    public void logEvent(String phoneNumber, String time, String event) throws SQLiteException {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_LOG_PHONE, phoneNumber);
            contentValues.put(COL_LOG_TIME, time);
            contentValues.put(COL_LOG_EVENT, event);
            db.insert(TABLE_NAME2, null, contentValues);
            db.close();
        } catch (SQLiteException e) {
            System.out.println("E's logEvent Error: " + e);
        }
    }// end logEvent method

    /**
     * checkAccountStatus() - compares phone number to Login table, looks for 'failed login' events w/in past 5 min
     *  account is unlocked = true, account is locked = false
     */
    public boolean checkAccountStatus(String phoneNumber) throws SQLiteException {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String now = Utilities.getTimeStr();
            Date nowTime = new Date();
            Date thenTime;
            String event = "failed login"; //looking for failed logins in db query return

            // convert string to DateTime obj
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            try{
                nowTime = dateFormat.parse(now);
            } catch (ParseException e) {
                System.out.println("checkLockout Error: " + e);
            }

            // set up db query for time of failed events - where phone# matches and event = 'Failed Login'
            String[] columns = {COL_LOG_TIME};
            String selection = COL_LOG_PHONE + " =? AND " + COL_LOG_EVENT + " =?";
            String[] selectionArgs = {phoneNumber, event};
            int failCount = 0;

            // query the db
            Cursor cursor = db.query(TABLE_NAME2, columns, selection, selectionArgs, null, null, null);

            // eval query results
            if (cursor.moveToFirst()) {
                int colIndex = cursor.getColumnIndex("time");
                do {
                    String failTimeStr;
                    failTimeStr = cursor.getString(colIndex);
                    try { // parse string to Date object
                        thenTime = dateFormat.parse(failTimeStr);
                    } catch (ParseException e) {
                        System.out.println("Error: Exception " + e);
                        return false; // assume locked if error
                    }

                    if (thenTime != null ){
                        // subtract then time from now time to determine difference in millis
                        long diff = nowTime.getTime() - thenTime.getTime();
                        //convert to mins
                        long diffMinutes = diff / (60 * 1000) % 60;

                        if (diffMinutes < 5) { // increment failCount if 'failed login' event in past 5 mins
                            failCount++;
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();

            if (failCount > 2) { // user is locked, return false
                return false;
            } else { // user is not locked
                return true;
            }
        } catch (SQLiteException e) {
            System.out.println("Error: Exception " + e);
            return false; // assume locked if error
        }
    } // end checkAccountStatus method
}
