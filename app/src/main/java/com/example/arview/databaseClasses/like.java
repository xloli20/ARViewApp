package com.example.arview.databaseClasses;


public class like {

    private String userName;
    private String likeDate;


    public like() {
    }

    public like(String userName, String likeDate) {
        this.userName = userName;
        this.likeDate = likeDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLikeDate() {
        return likeDate;
    }

    public void setLikeDate(String likeDate) {
        this.likeDate = likeDate;
    }

}
