package com.example.arview.databaseClasses;

public class userChat {

    private String chatId;
    private String otherUserId;

    public userChat() {
    }

    public userChat(String chatID, String otherUserID) {
        this.chatId = chatID;
        this.otherUserId = otherUserID;
    }

    @Override
    public String toString() {
        return "userChat{" +
                "chatId='" + chatId + '\'' +
                ", otherUserId='" + otherUserId + '\'' +
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
}


