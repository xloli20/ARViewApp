package com.example.arview.databaseClasses;

import android.location.Location;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;

public class profile {

    private String userName;
    private String name;
    private String userLocation;
    private String profilePhoto;
    private String profileDescription;
    private HashMap<String, Boolean> followers;
    private HashMap<String, Boolean> following;
    private HashMap<String, String> post;

    public profile(){
    }

    public profile(String userName, String name, String userLocation, String profilePhoto, String profileDescription, HashMap<String, Boolean> followers, HashMap<String, Boolean> following, HashMap<String, String> post) {
        this.userName = userName;
        this.name = name;
        this.userLocation = userLocation;
        this.profilePhoto = profilePhoto;
        this.profileDescription = profileDescription;
        this.followers = followers;
        this.following = following;
        this.post = post;
    }

    @NonNull
    @Override
    public String toString() {
        return "profile{" +
                "userName='" + userName + '\'' +
                ", name='" + name + '\'' +
                ", userLocation='" + userLocation + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                ", profileDescription='" + profileDescription + '\'' +
                ", followers=" + followers +
                ", following=" + following +
                ", post=" + post +
                '}';
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getProfileDescription() {
        return profileDescription;
    }

    public void setProfileDescription(String profileDescription) {
        this.profileDescription = profileDescription;
    }

    public HashMap<String, Boolean> getFollowers() {
        return followers;
    }

    public void setFollowers(HashMap<String, Boolean> followers) {
        this.followers = followers;
    }

    public HashMap<String, Boolean> getFollowing() {
        return following;
    }

    public void setFollowing(HashMap<String, Boolean> following) {
        this.following = following;
    }

    public HashMap<String, String> getPost() {
        return post;
    }

    public void setPost(HashMap<String, String> post) {
        this.post = post;
    }

}
