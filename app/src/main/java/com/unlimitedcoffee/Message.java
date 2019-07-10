package com.unlimitedcoffee;

public class Message {
    String number;
    String body;
    String timeStamp;
    String readStat;

    public Message(String number, String body, String timeStamp, String readStat) {
        this.number = number;
        this.body = body;
        this.timeStamp = timeStamp;
        this.readStat = readStat;
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

    public String getReadStat() {
        return readStat;
    }

    public void setReadStat(String readStat) {
        this.readStat = readStat;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
