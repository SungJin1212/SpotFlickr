package com.example.lee.spotflickr.Gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

public class Image {
    private String filename;
    private Bitmap img;
    private boolean isChecked = false;
    private boolean imgExists = false;
    private String mUrl;

    public Image(String filename, Bitmap img) {
        this.filename = filename;
        this.img = img;
        imgExists = true;
        mUrl="";
    }
    public void setExists(boolean t) {
        imgExists = t;
    }
    public boolean getExists() {
        return imgExists;
    }
    public String getFilename() {
        return filename;
    }

    public String getmUrl() {return mUrl;}
    public void setmUrl(String url){this.mUrl = url;}

    public boolean isChecked() {return isChecked;}
    public void toggleChecked() {isChecked = !isChecked;}
    public Bitmap getImg() {return img;}
}