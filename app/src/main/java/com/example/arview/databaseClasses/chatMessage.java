package com.example.arview.databaseClasses;

public class chatMessage {

    private String text;
    private String sender;
    private String photoURL ;
    //date and time
    private String postID;

    public chatMessage() {
    }

    public chatMessage(String text, String sender, String photoURL){
        this.text = text;
        this.sender = sender;
        this.photoURL = photoURL;
    }

    public chatMessage( String sender, String post) {
        this.sender = sender;
        this.postID = post;
}

    @Override
    public String toString() {
        return "chatMessage{" +
                "text='" + text + '\'' +
                ", sender='" + sender + '\'' +
                ", photoURL='" + photoURL + '\'' +
                '}';
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }
}
