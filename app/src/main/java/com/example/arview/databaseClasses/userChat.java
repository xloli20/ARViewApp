package com.example.arview.databaseClasses;

public class userChat {
    private String chatID;
    private String otherUserID;

    public userChat(){
    }
    public userChat(String chatID, String otherUserID) {
        this.chatID = chatID;
        this.otherUserID = otherUserID;
    }

    @Override
    public String toString() {
        return "userChat{" +
                "chatID='" + chatID + '\'' +
                ", otherUserID='" + otherUserID + '\'' +
                '}';
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getOtherUserID() {
        return otherUserID;
    }

    public void setOtherUserID(String otherUserID) {
        this.otherUserID = otherUserID;
    }
}
