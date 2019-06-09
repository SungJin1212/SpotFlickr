package com.example.lee.spotflickr.DatabaseClasses;

import java.util.HashMap;

public class HotspotPhoto {

    private String filename;
    private double longitude;
    private double latitude;

    public HotspotPhoto(){}
    public HotspotPhoto(String filename, double longitude, double latitude){
        this.filename = filename;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getFilename() {return filename;}
    public void setFilename(String filename) {this.filename = filename;}
    public double getLongitude() {return longitude;}
    public void setLongitude(double longitude) {this.longitude = longitude;}
    public void setLatitude(double latitude) {this.longitude = latitude;}
    public double getLatitude() {return latitude;}

}

