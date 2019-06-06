package com.example.lee.spotflickr.DatabaseClasses;

public class Hotspot {

    private String name;
    private int longitude;
    private int latitude;
    private String storageHash;

    public Hotspot() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public String getStorageHash() {
        return storageHash;
    }

    public void setStorageHash(String storageHash) {
        this.storageHash = storageHash;
    }
}

