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
    private Date postCreatedDate;
    private int likesCount;
    private int commentsCount;
    private String postEndDate;
    private String postEndTime;
    private boolean visibilty;
    private boolean personal;


    public post() {
    }


    public post(String postId, String ownerId, String postName, String postDesc, Location postLocation, Date postCreatedDate, int likesCount, int commentsCount,
                String postEndDate, String postEndTime, boolean visibilty, boolean personal) {
        this.postId = postId;
        this.ownerId = ownerId;
        this.postName = postName;
        this.postDesc = postDesc;
        this.postLocation = postLocation;
        this.postCreatedDate = postCreatedDate;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.postEndDate = postEndDate;
        this.postEndTime = postEndTime;
        this.visibilty = visibilty;
        this.personal = personal;
    }

    public post(String postId, String ownerId, String postName, String postDesc, Date postCreatedDate, int likesCount, int commentsCount,
                String postEndDate, String postEndTime, boolean visibilty, boolean personal) {
        this.postId = postId;
        this.ownerId = ownerId;
        this.postName = postName;
        this.postDesc = postDesc;
        this.postCreatedDate = postCreatedDate;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.postEndDate = postEndDate;
        this.postEndTime = postEndTime;
        this.visibilty = visibilty;
        this.personal = personal;
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

    public Date getPostCreatedDate() {
        return postCreatedDate;
    }

    public void setPostCreatedDate(Date postCreatedDate) {
        this.postCreatedDate = postCreatedDate;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getPostEndDate() {
        return postEndDate;
    }

    public void setPostEndDate(String postEndDate) {
        this.postEndDate = postEndDate;
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
}
