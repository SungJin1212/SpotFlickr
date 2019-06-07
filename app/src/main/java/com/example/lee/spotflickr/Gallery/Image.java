package com.example.lee.spotflickr.Gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

public class Image {
    private String filename;
    private String hash;
    private Bitmap img;
    private boolean isChecked = false;
    private boolean imgExists = false;

    public Image(String filename, String hash, Bitmap img) {
        this.filename = filename;
        this.hash = hash;
        this.img = img;
        imgExists = true;
    }
    public void setExists(boolean t) {
        imgExists = t;
    }
    public boolean getExists() {
        return imgExists;
    }
    public String getHash() {
        return hash;
    }


    public boolean isChecked() {return isChecked;}
    public void toggleChecked() {isChecked = !isChecked;}
    public Bitmap getImg() {return img;}
}