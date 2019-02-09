package com.example.arview.databaseClasses;

import android.location.Location;
import android.net.Uri;

import java.util.Date;

public class postClass {
    private String postId;
    private String userName;
    private Location postLocation;
    private String postName;
    private String postDescription;
    private Uri postImage;
    private Date postDate;
    private String visiblity;
    private String[] visibiltyCustom;
    private String postEnd; // on date no limit after
    private Date postEndDate;
    private int postEndAfter;

    public postClass(){}
    public postClass(String postId, String userName, Location postLocation, String postName, String postDescription, Uri postImage, Date postDate, String visiblity, String[] visibiltyCustom, String postEnd, Date postEndDate, int postEndAfter ){
        this.postId = postId;

    }



    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Location getPostLocation() {
        return postLocation;
    }

    public void setPostLocation(Location postLocation) {
        this.postLocation = postLocation;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public Uri getPostImage() {
        return postImage;
    }

    public void setPostImage(Uri postImage) {
        this.postImage = postImage;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public String getVisiblity() {
        return visiblity;
    }

    public void setVisiblity(String visiblity) {
        this.visiblity = visiblity;
    }

    public String[] getVisibiltyCustom() {
        return visibiltyCustom;
    }

    public void setVisibiltyCustom(String[] visibiltyCustom) {
        this.visibiltyCustom = visibiltyCustom;
    }

    public String getPostEnd() {
        return postEnd;
    }

    public void setPostEnd(String postEnd) {
        this.postEnd = postEnd;
    }

    public Date getPostEndDate() {
        return postEndDate;
    }

    public void setPostEndDate(Date postEndDate) {
        this.postEndDate = postEndDate;
    }

    public int getPostEndAfter() {
        return postEndAfter;
    }

    public void setPostEndAfter(int postEndAfter) {
        this.postEndAfter = postEndAfter;
    }







}
