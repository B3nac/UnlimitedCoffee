package com.unlimitedcoffee;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MessageHistAdapter extends BaseAdapter {

    LayoutInflater mInflater;   // adapts the layout to the
    //data array information
    ArrayList<String> phoneNumbers;
    ArrayList<String> messages;
    ArrayList<String> date;


    public MessageHistAdapter(Context c, ArrayList<String> s1, ArrayList<String> s2, ArrayList<String> s3 ){
        phoneNumbers = s1;
        messages = s2;
        date = s3;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return phoneNumbers.size();    // returns the number of items in the string array provided
    }

    @Override
    public Object getItem(int position) {   // returns the items at the index provided
        return phoneNumbers.get(position);
    }

    @Override
    public long getItemId(int position) {   //
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.messagelvdetail, null); // passing the mylistview to inflater
        //assign new text views to listviewdetail textviews
        TextView phoneNumTextView = (TextView) v.findViewById(R.id.phoneNumTextView);
        TextView messageTextView = (TextView) v.findViewById(R.id.messageTextView);
        TextView dateTextView = (TextView) v.findViewById(R.id.dateTextView);
        //fields to hold indexed data from the string array
        String phoneNum = phoneNumbers.get(position);
        String msg = messages.get(position);
        String dte = date.get(position);
        // set the textviews with the string data
        phoneNumTextView.setText(phoneNum);
        messageTextView.setText(msg);
        dateTextView.setText(formatTime(dte));
        return v; // returns the view
    }

    private String formatTime(String time){
        final String DATE_FORMAT_1 = "MM/dd hh:mm a";
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_1);
        dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
        String today = dateFormat.format(Long.parseLong(time));
        return today;
    }
}
