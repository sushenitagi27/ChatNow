package com.example.chatnow;

public class Messages {
    String message;
    String senderid;
    long timestamp;
    String currenttime;

    public Messages() {
    }

    public Messages(String message, String senderid, long timestamp, String currenttime) {
        this.message = message;
        this.senderid = senderid;
        this.timestamp = timestamp;
        this.currenttime = currenttime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCurrenttime() {
        return currenttime;
    }

    public void setCurrenttime(String currenttime) {
        this.currenttime = currenttime;
    }
}
