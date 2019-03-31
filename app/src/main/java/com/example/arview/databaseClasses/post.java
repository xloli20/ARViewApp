package com.example.arview.databaseClasses;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class post {

    private String postId;
    private String ownerId;
    private String postName;
    private String postDesc;
    private Location postLocation;
    private String postCreatedDate;
    private String likes;
    private String comments;
    private String postEndTime;
    private boolean visibilty;
    private boolean personal;


    public post() {
    }


    public post(String postId, String ownerId, String postName, String postDesc, Location postLocation, String postCreatedDate, String likesCount, String commentsCount,
                String postEndTime, boolean visibilty, boolean personal) {
        this.postId = postId;
        this.ownerId = ownerId;
        this.postName = postName;
        this.postDesc = postDesc;
        this.postLocation = postLocation;
        this.postCreatedDate = postCreatedDate;
        this.likes = likesCount;
        this.comments = commentsCount;
        this.postEndTime = postEndTime;
        this.visibilty = visibilty;
        this.personal = personal;
    }

    public post(String postId, String ownerId, String postName, String postDesc, String postCreatedDate, String likesCount, String commentsCount,
                String postEndTime, boolean visibilty, boolean personal) {
        this.postId = postId;
        this.ownerId = ownerId;
        this.postName = postName;
        this.postDesc = postDesc;
        this.postCreatedDate = postCreatedDate;
        this.likes = likesCount;
        this.comments = commentsCount;
        this.postEndTime = postEndTime;
        this.visibilty = visibilty;
        this.personal = personal;
    }

    public post(String postId, String ownerId, String postName, String postDesc, String postCreatedDate,
                String postEndTime, boolean visibilty, boolean personal) {
        this.postId = postId;
        this.ownerId = ownerId;
        this.postName = postName;
        this.postDesc = postDesc;
        this.postCreatedDate = postCreatedDate;
        this.postEndTime = postEndTime;
        this.visibilty = visibilty;
        this.personal = personal;
    }

    public post(String postId, String ownerId, String postName, String postDesc, String postCreatedDate,
                String postEndTime) {
        this.postId = postId;
        this.ownerId = ownerId;
        this.postName = postName;
        this.postDesc = postDesc;
        this.postCreatedDate = postCreatedDate;
        this.postEndTime = postEndTime;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }

    public Location getPostLocation() {
        return postLocation;
    }

    public void setPostLocation(Location postLocation) {
        this.postLocation = postLocation;
    }

    public String getPostCreatedDate() {
        return postCreatedDate;
    }

    public void setPostCreatedDate(String postCreatedDate) {
        this.postCreatedDate = postCreatedDate;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getPostEndTime() {
        return postEndTime;
    }

    public void setPostEndTime(String postEndTime) {
        this.postEndTime = postEndTime;
    }

    public boolean isVisibilty() {
        return visibilty;
    }

    public void setVisibilty(boolean visibilty) {
        this.visibilty = visibilty;
    }

    public boolean isPersonal() {
        return personal;
    }

    public void setPersonal(boolean personal) {
        this.personal = personal;
    }

    @Override
    public String toString() {
        return "post{" +
                "postId='" + postId + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", postName='" + postName + '\'' +
                ", postDesc='" + postDesc + '\'' +
                ", postLocation=" + postLocation +
                ", postCreatedDate='" + postCreatedDate + '\'' +
                ", likes='" + likes + '\'' +
                ", comments='" + comments + '\'' +
                ", postEndTime='" + postEndTime + '\'' +
                ", visibilty=" + visibilty +
                ", personal=" + personal +
                '}';
    }
}
