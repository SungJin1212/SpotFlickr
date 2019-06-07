package com.example.lee.spotflickr.DatabaseClasses;

import java.util.HashMap;

public class HotspotList {

    //private int  id;
    private String name;
    private String userEmail;
    private HashMap<String, Hotspot> hotspots;
    //private String description;

    public HotspotList() {
    }
    public HotspotList(String name, String userEmail, HashMap<String, Hotspot> hotspots) {
        this.name = name;
        this.userEmail = userEmail;
        this.hotspots = hotspots;
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

    public HashMap<String, Hotspot> getHotspots() { return hotspots; }

    public void setHotspots(HashMap<String, Hotspot> hotspots) { this.hotspots = hotspots; }
}

