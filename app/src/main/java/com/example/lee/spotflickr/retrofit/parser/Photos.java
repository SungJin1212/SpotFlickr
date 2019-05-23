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
    public String total;
    @SerializedName("photo")
    public Photo[] photo;

    public Photos(int page, int pages, int perpage, String total, Photo[] photo) {
        this.page = page;
        this.pages = pages;
        this.perpage = perpage;
        this.total = total;
        this.photo = photo;
    }

}