package com.unlimitedcoffee;

import java.util.ArrayList;

public class Conversation {
    String number;
    ArrayList<String> messages;

    public Conversation (String number, ArrayList<String>messages ){
        this.number = number;
        this.messages= messages;
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


}