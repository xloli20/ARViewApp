package com.example.arview.databaseClasses;

import com.google.android.gms.maps.model.LatLng;

import androidx.annotation.NonNull;

public class nearPost {

    private post post;
    private String ownerName;
    private String profilePhoto;
    private String destinace;
    private LatLng location;

    public nearPost() {
    }

    public nearPost(com.example.arview.databaseClasses.post post, String ownerName, String profilePhoto, String destinace, LatLng location) {
        this.post = post;
        this.ownerName = ownerName;
        this.profilePhoto = profilePhoto;
        this.destinace = destinace;
        this.location = location;
    }

    public com.example.arview.databaseClasses.post getPost() {
        return post;
    }

    public void setPost(com.example.arview.databaseClasses.post post) {
        this.post = post;
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

    @NonNull
    @Override
    public String toString() {
        return "nearPost{" +
                "post=" + post +
                ", ownerName='" + ownerName + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                ", destinace='" + destinace + '\'' +
                ", location=" + location +
                '}';
    }
}
