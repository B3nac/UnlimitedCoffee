package com.unlimitedcoffee;

public class Message {
    String number;
    String body;
    String timeStamp;

    public Message(String number, String body, String timeStamp) {
        this.number = number;
        this.body = body;
        this.timeStamp = timeStamp;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBody() {
        return body;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
