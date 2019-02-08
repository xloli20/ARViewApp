package com.example.arview.databaseClasses;

import java.util.Date;

public class comment {

    //private String postId;
    private String userName;
    private String commentDate;
    private String comment;

    public comment() {
    }

    public comment(String userName, String commentDate, String comment) {
        this.userName = userName;
        this.commentDate = commentDate;
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
