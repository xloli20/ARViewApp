package com.example.arview.profile;

import android.location.Location;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.Date;
import java.util.List;

public class profileClass {
    private String userName;
    private String name;
    private ContactsContract.CommonDataKinds.Email email;
    private String password;
    private Location userLocation;
    private Uri profilePhoto;
    private Uri backgroundPhoto;
    private String profileDescription;
    private Date createdAt;
    private Date updatedAt;
    private Date followRequestDate;
    private String followRequestName;


    public profileClass(){
    }
    public profileClass(String userName, ContactsContract.CommonDataKinds.Email email, String password, Date createdAt  ){
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
    }

    public void editeProfile( Uri profilePhoto, Uri backgroundPhoto, String name,String profileDescription  ){
        if (profilePhoto != null){
            this.profilePhoto = profilePhoto;
        }
        if (backgroundPhoto != null){
            this.backgroundPhoto = backgroundPhoto;
        }
        if (name != null){
            this.name = name;
        }
        if (profileDescription != null){
            this.profileDescription = profileDescription;
        }
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

    public ContactsContract.CommonDataKinds.Email getEmail() {
        return email;
    }

    public void setEmail(ContactsContract.CommonDataKinds.Email email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }

    public Uri getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(Uri profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public Uri getBackgroundPhoto() {
        return backgroundPhoto;
    }

    public void setBackgroundPhoto(Uri backgroundPhoto) {
        this.backgroundPhoto = backgroundPhoto;
    }

    public String getProfileDescription() {
        return profileDescription;
    }

    public void setProfileDescription(String profileDescription) {
        this.profileDescription = profileDescription;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getFollowRequestDate() {
        return followRequestDate;
    }

    public void setFollowRequestDate(Date followRequestDate) {
        this.followRequestDate = followRequestDate;
    }

    public String getFollowRequestName() {
        return followRequestName;
    }

    public void setFollowRequestName(String followRequestName) {
        this.followRequestName = followRequestName;
    }
}
