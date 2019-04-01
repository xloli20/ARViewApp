package com.example.arview.databaseClasses;

import java.util.Date;

public class comment {

    private String CommentID;
    private String userID;
    private String userName;
    private String comment;
    private String commentDate;
    private String likes;

    private String PostID;
    private String PostPath;

    public comment() {
    }

    public comment(String userID, String userName, String comment, String commentDate) {
        this.userID = userID;
        this.userName = userName;
        this.comment = comment;
        this.commentDate = commentDate;
    }

    public comment(String commentID, String userID, String userName, String comment, String commentDate, String likes, String postID, String postPath) {
        CommentID = commentID;
        this.userID = userID;
        this.userName = userName;
        this.comment = comment;
        this.commentDate = commentDate;
        this.likes = likes;
        PostID = postID;
        PostPath = postPath;
    }

    public String getCommentID() {
        return CommentID;
    }

    public void setCommentID(String commentID) {
        CommentID = commentID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getPostID() {
        return PostID;
    }

    public void setPostID(String postID) {
        PostID = postID;
    }

    public String getPostPath() {
        return PostPath;
    }

    public void setPostPath(String postPath) {
        PostPath = postPath;
    }

    @Override
    public String toString() {
        return "comment{" +
                "CommentID='" + CommentID + '\'' +
                ", userID='" + userID + '\'' +
                ", userName='" + userName + '\'' +
                ", comment='" + comment + '\'' +
                ", commentDate='" + commentDate + '\'' +
                ", likes='" + likes + '\'' +
                ", PostID='" + PostID + '\'' +
                ", PostPath='" + PostPath + '\'' +
                '}';
    }
}