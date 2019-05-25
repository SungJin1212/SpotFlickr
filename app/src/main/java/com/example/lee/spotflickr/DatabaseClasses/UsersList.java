package com.example.lee.spotflickr.DatabaseClasses;

public class UsersList {

    private String userID;
    private String HotspotListID;

    public UsersList() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getHotspotListID() {
        return HotspotListID;
    }

    public void setHotspotListID(String hotspotListID) {
        HotspotListID = hotspotListID;
    }
}

