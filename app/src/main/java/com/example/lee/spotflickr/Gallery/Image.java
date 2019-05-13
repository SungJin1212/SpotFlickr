package com.example.lee.spotflickr.Gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

public class Image {
    private String path;
    private final int THUMBSIZE = 64;
    private Bitmap img;
    private boolean isChecked = false;

    public Image(String filename, boolean makeThumb) {
        this.path = filename;
        if(makeThumb) {
            img = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path),
                    THUMBSIZE, THUMBSIZE);
        } else {
            img = BitmapFactory.decodeFile(path);
        }
    }
    public boolean isChecked() {return isChecked;}
    public void toggleChecked() {isChecked = !isChecked;}
    public Bitmap getImg() {return img;}
    public String getPath() {return path;}
}