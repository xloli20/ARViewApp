package com.example.arview.databaseClasses;

import androidx.annotation.NonNull;

public class userChatProfile {
    private userChat userChat;
    private profile profile;

    public userChatProfile() {
    }

    public userChatProfile(com.example.arview.databaseClasses.userChat userChat, com.example.arview.databaseClasses.profile profile) {
        this.userChat = userChat;
        this.profile = profile;
    }

    @NonNull
    @Override
    public String toString() {
        return "userChatProfile{" +
                "userChat=" + userChat +
                ", profile=" + profile +
                '}';
    }

    public com.example.arview.databaseClasses.userChat getUserChat() {
        return userChat;
    }

    public void setUserChat(com.example.arview.databaseClasses.userChat userChat) {
        this.userChat = userChat;
    }

    public com.example.arview.databaseClasses.profile getProfile() {
        return profile;
    }

    public void setProfile(com.example.arview.databaseClasses.profile profile) {
        this.profile = profile;
    }
}
