package com.example.lee.spotflickr.retrofit.parser;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Photos {
    @SerializedName("page")
    public int page;
    @SerializedName("pages")
    public int pages;
    @SerializedName("perpage")
    public int perpage;
    @SerializedName("total")
<<<<<<< HEAD
    public int total;
    @SerializedName("photo")
    public Photo[] photo;

    public Photos(int page, int pages, int perpage, int total, Photo[] photo) {
=======
    public String total;
    @SerializedName("photo")
    public Photo[] photo;

    public Photos(int page, int pages, int perpage, String total, Photo[] photo) {
>>>>>>> c105167a002adcf7521b8e5d777979608bba239a
        this.page = page;
        this.pages = pages;
        this.perpage = perpage;
        this.total = total;
        this.photo = photo;
    }

}