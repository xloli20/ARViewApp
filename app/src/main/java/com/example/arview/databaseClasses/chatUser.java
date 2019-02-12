package com.example.arview.databaseClasses;

public class chatUser {

    private String chatId;
    private String otherUserId;
    private profile otherUserProfile;

    public chatUser() {
    }

    public chatUser(String chatId, String otherUserId, profile otherUserProfile) {
        this.chatId = chatId;
        this.otherUserId = otherUserId;
        this.otherUserProfile = otherUserProfile;
    }

    @Override
    public String toString() {
        return "chatUser{" +
                "chatId='" + chatId + '\'' +
                ", otherUserId='" + otherUserId + '\'' +
                ", otherUserProfile=" + otherUserProfile +
                '}';
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public profile getOtherUserProfile() {
        return otherUserProfile;
    }

    public void setOtherUserProfile(profile otherUserProfile) {
        this.otherUserProfile = otherUserProfile;
    }

}
