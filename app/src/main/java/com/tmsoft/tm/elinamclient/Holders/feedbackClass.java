package com.tmsoft.tm.elinamclient.Holders;

public class feedbackClass {
    private String userId, feedback;
    private long time;

    public feedbackClass() {
    }

    public feedbackClass(String userId, String feedback, long time) {
        this.userId = userId;
        this.feedback = feedback;
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
