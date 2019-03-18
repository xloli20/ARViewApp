package com.example.arview.databaseClasses;

import com.google.android.gms.maps.model.LatLng;

public class nearPost {

    private String postId;
    private String postName;
    private String likeCount;
    private String ownerId;
    private String ownerName;
    private String profilePhoto;
    private String destinace;
    private LatLng location;

    public nearPost() {
    }

    public nearPost(String postId, String postName, String likeCount, String ownerId, String ownerName, String profilePhoto, String destinace) {
        this.postId = postId;
        this.postName = postName;
        this.likeCount = likeCount;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.profilePhoto = profilePhoto;
        this.destinace = destinace;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getDestinace() {
        return destinace;
    }

    public void setDestinace(String destinace) {
        this.destinace = destinace;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "nearPost{" +
                "postId='" + postId + '\'' +
                ", postName='" + postName + '\'' +
                ", likeCount='" + likeCount + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                ", destinace='" + destinace + '\'' +
                ", location=" + location +
                '}';
    }
}
