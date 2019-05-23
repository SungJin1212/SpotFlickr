package com.example.lee.spotflickr.retrofit.parser;

import android.support.annotation.StringRes;

import com.google.gson.annotations.SerializedName;


public class PhotoList {

    @SerializedName("photos")
    public Photos photos;
    @SerializedName("stat")
    public String stat;


}
