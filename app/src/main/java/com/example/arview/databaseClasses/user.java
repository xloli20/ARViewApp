package com.example.arview.databaseClasses;

import android.provider.ContactsContract;

import com.firebase.ui.auth.data.model.PhoneNumber;

import java.util.Date;

public class user {

    private String userId;
    private String userName;
    private String name;
    private String email;
    private String createdAt;
    private String updatedAt;
    private long phoneNumber;

    public user() {
    }

    public user(String userId, String userName, String name, String email, String createdAt, String updatedAt, long phoneNumber) {
        this.userId = userId;
        this.userName = userName;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.phoneNumber = phoneNumber;
    }


    @Override
    public String toString() {
        return "user{" +
                "userName='" + userName + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", phoneNumber=" + phoneNumber +
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
