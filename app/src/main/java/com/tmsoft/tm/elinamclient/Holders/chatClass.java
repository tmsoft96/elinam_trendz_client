package com.tmsoft.tm.elinamclient.Holders;

public class chatClass {
    private String UserId, OtherUserId, content, userName, userImage, date, time;

    public chatClass() {
    }

    public chatClass(String UserId, String OtherUserId, String content, String userName, String date, String time, String userImage) {
        this.UserId = UserId;
        this.OtherUserId = OtherUserId;
        this.content = content;
        this.userName = userName;
        this.date = date;
        this.time = time;
        this.userImage = userImage;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getOtherUserId() {
        return OtherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        OtherUserId = otherUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
