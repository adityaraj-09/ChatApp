package com.example.insien.ModelClass;

public class Messages {
    String message;
    String senderId;
    long timeStamp;
    boolean isseen;
    String type;

    public Messages() {
    }


    public Messages(String message, String senderId, long timeStamp, boolean isseen, String type) {
        this.message = message;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
        this.isseen = isseen;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
