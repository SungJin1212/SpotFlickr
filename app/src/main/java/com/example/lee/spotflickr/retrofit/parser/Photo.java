package com.example.lee.spotflickr.retrofit.parser;

import com.google.gson.annotations.SerializedName;

public class Photo {
    @SerializedName("id")
    private String id;
    @SerializedName("owner")
    private String owner;
    @SerializedName("secret")
    private String secret;

    public void setTitle(String title) {
        this.title = title;
    }

    @SerializedName("server")
    private String server;
    @SerializedName("farm")
    private int farm;
    @SerializedName("title")
    private String title;
    @SerializedName("ispublic")
    private int ispublic;
    @SerializedName("isfriend")
    private int isfriend;
    @SerializedName("isfamily")
    private int isfamily;
    @SerializedName("latitude")
    private float latitude;
    @SerializedName("longitude")
    private float longitude;
    @SerializedName("accuracy")
    private int accuracy;
    @SerializedName("context")
    private int context;
    @SerializedName("place_id")
    private String place_id;
    @SerializedName("woeid")
    private String woeid;
    @SerializedName("geo_is_family")
    private int geo_is_family;
    @SerializedName("geo_is_friend")
    private int geo_is_friend;
    @SerializedName("geo_is_contact")
    private int geo_is_contact;
    @SerializedName("geo_is_public")
    private int geo_is_public;
    @SerializedName("url_s")
    private String url_s;


    @SerializedName("height_s")
    private String height_s;
    @SerializedName("width_s")
    private String width_s;

    public Photo(String id, String owner, String secret, String server, int farm, String title, int ispublic, int isfriend, int isfamily, float latitude, float longitude, int accuracy, int context, String place_id, String woeid, int geo_is_family, int geo_is_friend, int geo_is_contact, int geo_is_public, String url_s, String height_s, String width_s) {
        this.id = id;
        this.owner = owner;
        this.secret = secret;
        this.server = server;
        this.farm = farm;
        this.title = title;
        this.ispublic = ispublic;
        this.isfriend = isfriend;
        this.isfamily = isfamily;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.context = context;
        this.place_id = place_id;
        this.woeid = woeid;
        this.geo_is_family = geo_is_family;
        this.geo_is_friend = geo_is_friend;
        this.geo_is_contact = geo_is_contact;
        this.geo_is_public = geo_is_public;
        this.url_s = url_s;
        this.height_s = height_s;
        this.width_s = width_s;
    }

    public String getUrl_s() {
        return url_s;
    }
    public String getTitle() {
        return title;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }


}
