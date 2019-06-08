package com.example.lee.spotflickr.DatabaseClasses;

import java.util.HashMap;

public class Hotspot {

    private String name;
    private double longitude;
    private double latitude;
    private HashMap<String, HotspotPhoto> photos;

    public Hotspot() {
    }
    public Hotspot(String name, double longitude, double latitude, HashMap<String, HotspotPhoto> photos) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.photos = photos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

}

