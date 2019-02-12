package com.example.arview.databaseClasses;

import android.provider.ContactsContract;

import com.firebase.ui.auth.data.model.PhoneNumber;

import java.util.Date;

public class users {

    private String userName;
    private String email;
    private String createdAt;
    private String updatedAt;
    private long phoneNumber;

    public users() {
    }

    public users( String userName, String email, String createdAt, String updatedAt, long phoneNumber) {
        this.userName = userName;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.phoneNumber = phoneNumber;
    }


    @Override
    public String toString() {
        return "users{" +
                "userName='" + userName + '\'' +
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


}
