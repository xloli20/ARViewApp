package com.example.arview.databaseClasses;

public class post {

    //private String postId;
    private String ownerId;
    private String postName;
    private String postPhoto;
    private String postDesc;
    private String postLocation;
    private String postCreatedDate;

    public post() {
    }

    public post(String ownerId, String postName, String postPhoto, String postDesc, String postLocation, String postCreatedDate) {
        this.ownerId = ownerId;
        this.postName = postName;
        this.postPhoto = postPhoto;
        this.postDesc = postDesc;
        this.postLocation = postLocation;
        this.postCreatedDate = postCreatedDate;
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

    public String getPostPhoto() {
        return postPhoto;
    }

    public void setPostPhoto(String postPhoto) {
        this.postPhoto = postPhoto;
    }

    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }

    public String getPostLocation() {
        return postLocation;
    }

    public void setPostLocation(String postLocation) {
        this.postLocation = postLocation;
    }

    public String getPostCreatedDate() {
        return postCreatedDate;
    }

    public void setPostCreatedDate(String postCreatedDate) {
        this.postCreatedDate = postCreatedDate;
    }
}
