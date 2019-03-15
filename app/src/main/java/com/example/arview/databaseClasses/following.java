package com.example.arview.databaseClasses;

public class following {
    private String email;
    private String uid;
    private String profilePhoto;


    public following(){
    }

    public following(String email, String uid, String profilePhoto) {
        this.email = email;
        this.uid = uid;
        this.profilePhoto = profilePhoto;
    }

    public String getUid(){
        return uid;
    }
    public void setUid(String uid){
        this.uid = uid;
    }

    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}