package com.unlimitedcoffee;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;

public class PNDatabaseHelper extends SQLiteOpenHelper {

    //Database name and columns
    public static final String DATABASE_NAME = "phone_Number_List";
    public static final String TABLE_NAME = "phone_Numbers";
    public static final String COL_ID = "ID";
    public static final String COL_PHONE_NUMBER = "phone_numbers";

    /**
     * This method helps create the data base
     */
     private String CREATE_NUMBER_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_PHONE_NUMBER + " STRING)";
    //Drop SQL Table
     private String DROP_NUMBER_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    /**
     * Class constructor
     * @param context
     */
    public PNDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_NUMBER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_NUMBER_TABLE);
        onCreate(sqLiteDatabase);
    }

    //Method to add new phone number from the list
    public void addPhoneNumber(String phoneNumber) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PHONE_NUMBER, phoneNumber);
        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }

    //Method to delete phone number from teh list
    public void deletePhoneNumber(String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by phone number
        db.delete(TABLE_NAME, COL_PHONE_NUMBER + " = ?",
                new String[]{phoneNumber});
        db.close();
    }


    public void deleteAllPhoneNumbers(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }


    //Checks if phone number is in the table
    public boolean containsPhoneNumber(String phoneNumber) {
        String[] columns = {COL_ID};
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_PHONE_NUMBER + "=?";
        String[] selectionArgs = {phoneNumber};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        db.close();
        if (count > 0)
            return true;
        else
            return false;
    }

    public Cursor getAllPhoneNumber() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        return cursor;

    }

    public boolean isEmpty(){
        String[] columns = {COL_ID, COL_PHONE_NUMBER};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
        int count = cursor.getCount();
        db.close();
        if (count > 0)
            return false;
        else
            return true;
    }




}
