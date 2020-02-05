package com.tmsoft.tm.elinamclient.Holders;

public class commentElement {
    private String commentUserId, commentUsername, commentProfilePicture, commentDate, commentTime, comment;

    public commentElement() {
    }

    public commentElement(String commentUserId, String commentUsername, String commentProfilePicture, String commentDate, String commentTime, String comment) {
        this.commentUserId = commentUserId;
        this.commentUsername = commentUsername;
        this.commentProfilePicture = commentProfilePicture;
        this.commentDate = commentDate;
        this.commentTime = commentTime;
        this.comment = comment;
    }

    public String getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(String commentUserId) {
        this.commentUserId = commentUserId;
    }

    public String getCommentUsername() {
        return commentUsername;
    }

    public void setCommentUsername(String commentUsername) {
        this.commentUsername = commentUsername;
    }

    public String getCommentProfilePicture() {
        return commentProfilePicture;
    }

    public void setCommentProfilePicture(String commentProfilePicture) {
        this.commentProfilePicture = commentProfilePicture;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
