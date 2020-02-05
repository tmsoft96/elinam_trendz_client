package com.tmsoft.tm.elinamclient.Holders;

public class MessagesClass {
    private String message, type;
    private long time;
    private String from, actualSenderId;

    public MessagesClass() {
    }

    public MessagesClass(String message, String type, long time, String from, String actualSenderId) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.from = from;
        this.actualSenderId = actualSenderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getActualSenderId() {
        return actualSenderId;
    }

    public void setActualSenderId(String actualSenderId) {
        this.actualSenderId = actualSenderId;
    }
}
