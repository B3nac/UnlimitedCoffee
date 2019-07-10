package com.unlimitedcoffee;

import java.util.ArrayList;

public class Conversation {
    private String number;
    private ArrayList<String> messages;
    private ArrayList<String> timeStamp;
    private ArrayList<String> readStat;

    public Conversation (String number, ArrayList<String>messages, ArrayList<String> timeStamp, ArrayList<String> readStat){
        this.number = number;
        this.messages= messages;
        this.timeStamp = timeStamp;
        this.readStat = readStat;
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

    public String findLastReadStat() {
        return this.readStat.get(0);
    }

}