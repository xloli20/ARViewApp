package com.example.arview.databaseClasses;

public class chats {
    //private String chatId;
    private String otherUserId;

    public chats() {
    }

    public chats(String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }
}
