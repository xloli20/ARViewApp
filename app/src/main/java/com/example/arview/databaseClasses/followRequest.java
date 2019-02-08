package com.example.arview.databaseClasses;

public class followRequest {
    private String followRequestName;

    private String accept;

    public followRequest() {
    }

    public followRequest(String followRequestName, String accept) {
        this.followRequestName = followRequestName;
        this.accept = accept;
    }


    public String getFollowRequestName() {
        return followRequestName;
    }

    public void setFollowRequestName(String followRequestName) {
        this.followRequestName = followRequestName;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

}
