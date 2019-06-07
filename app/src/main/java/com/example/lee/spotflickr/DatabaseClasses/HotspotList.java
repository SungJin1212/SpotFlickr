package com.example.lee.spotflickr.DatabaseClasses;

public class HotspotList {

    //private int  id;
    private String name;
    private String userEmail;
    //private String description;

    public HotspotList() {
    }
    public HotspotList(String name, String userEmail) {
        this.name = name;
        this.userEmail = userEmail;
    }

    //public int getID() { return id; }

    //public void setID(int id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserEmail() { return userEmail; }

    public void setUserEmail(String userEmail) {this.userEmail = userEmail; }

    //public String getDescription() { return description; }

    //public void setDescription(String description) { this.description = description; }
}

