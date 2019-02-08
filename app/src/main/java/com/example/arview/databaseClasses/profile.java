package com.example.arview.databaseClasses;

import android.location.Location;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.Date;
import java.util.List;

public class profile {


    private String userName;
    private String userLocation;
    private String profilePhoto;
    private String profileDescription;

    private int followers;
    private int following;
    private int post;


    public profile(){
    }

    public profile(String userName, String userLocation, String profilePhoto, String profileDescription, int followers, int following, int post) {
        this.userName = userName;
        this.userLocation = userLocation;
        this.profilePhoto = profilePhoto;
        this.profileDescription = profileDescription;
        this.followers = followers;
        this.following = following;
        this.post = post;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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


    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }

}
