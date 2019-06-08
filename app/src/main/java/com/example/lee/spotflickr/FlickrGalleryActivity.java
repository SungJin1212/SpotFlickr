package com.example.lee.spotflickr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.lee.spotflickr.DatabaseClasses.HotspotPhoto;
import com.example.lee.spotflickr.Gallery.GalleryAdapter;
import com.example.lee.spotflickr.Gallery.Image;

import java.util.ArrayList;


public class FlickrGalleryActivity extends AppCompatActivity {
    private Context context = this;
    Button btnSaveHotspot;
    private GridView gvGallery;
    private GalleryAdapter galleryAdapter;
    ArrayList<Image> imgs;
    // url parameter
    ArrayList<String> url = new ArrayList<String>();
    String temp = "";
    // location parameter
    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photolistactivity);


        init();
    }



    private void init(){

        // get parameter
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        url = extras.getStringArrayList("Url");
        longitude = extras.getDouble("Longitude");
        latitude = extras.getDouble("Latitude");
        // button
        btnSaveHotspot = findViewById(R.id.btnSaveHotspot);
        gvGallery = (GridView)findViewById(R.id.gvFlickr);

        imgs = new ArrayList<Image>();
        galleryAdapter = new GalleryAdapter(getApplicationContext(),imgs);
        gvGallery.setAdapter(galleryAdapter);
        gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                .getLayoutParams();
        mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

        Log.d("Debug", "HJ Debug: url size:"+url.size());
        Log.d("Debug", "HJ Debug: longitude:"+longitude);

        for(String s : url) {
            Log.d("Debug", "HJ Debug:url : "+s);
            temp += s;
        }

    }



}
