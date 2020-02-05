package com.tmsoft.tm.elinamclient.Holders;

public class publicNotificationClass {
    private String title, read, userId, activityIoGo, message, messageTitle, messageTo, postKey;
    private long time;

    public publicNotificationClass() {
    }

    public publicNotificationClass(String title, String read, String userId, String activityIoGo, String message, String messageTitle, String messageTo, String postKey, long time) {
        this.title = title;
        this.read = read;
        this.userId = userId;
        this.activityIoGo = activityIoGo;
        this.message = message;
        this.messageTitle = messageTitle;
        this.messageTo = messageTo;
        this.postKey = postKey;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActivityIoGo() {
        return activityIoGo;
    }

    public void setActivityIoGo(String activityIoGo) {
        this.activityIoGo = activityIoGo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getMessageTo() {
        return messageTo;
    }

    public void setMessageTo(String messageTo) {
        this.messageTo = messageTo;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
