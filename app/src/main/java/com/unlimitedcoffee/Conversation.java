package com.unlimitedcoffee;

import java.util.ArrayList;

public class Conversation {
    String number;
    ArrayList<String> messages;
    ArrayList<String> timeStamp;

    public Conversation (String number, ArrayList<String>messages, ArrayList<String> timeStamp){
        this.number = number;
        this.messages= messages;
        this.timeStamp = timeStamp;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public String findLastMessage(){
        return this.messages.get(0);
    }

    public String findLastTimeStamp(){
        return this.timeStamp.get(0);
    }



}