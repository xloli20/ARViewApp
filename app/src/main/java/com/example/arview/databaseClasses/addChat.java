package com.example.arview.databaseClasses;

public class addChat {
    private String username;
    private String name;
    private String uid;
    private String profilePhoto;
    private String ChatId;


    public addChat(){
    }

    public addChat(String email, String uid, String profilePhoto) {
        this.username = email;
        this.uid = uid;
        this.profilePhoto = profilePhoto;
    }

    public addChat(String username, String name, String uid, String profilePhoto) {
        this.username = username;
        this.name = name;
        this.uid = uid;
        this.profilePhoto = profilePhoto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChatId() {
        return ChatId;
    }

    public void setChatId(String chatId) {
        ChatId = chatId;
    }

    @Override
    public String toString() {
        return "following{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", uid='" + uid + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                '}';
    }
}