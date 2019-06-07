package com.example.lee.spotflickr.DatabaseClasses;

import java.util.HashMap;

public class HotspotPhoto {

    private String filename;
    private String storageHash;
    private double longitude;
    private double latitude;

    public HotspotPhoto(){}
    public HotspotPhoto(String filename, String storageHash, double longitude, double latitude){
        this.filename = filename;
        this.storageHash = storageHash;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getFilename() {return filename;}
    public void setFilename(String filename) {this.filename = filename;}
    public String getStorageHash() {return storageHash;}
    public void setStorageHash(String storageHash) {this.storageHash = storageHash;}

}

