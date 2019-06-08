package com.example.lee.spotflickr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lee.spotflickr.DatabaseClasses.HotspotPhoto;
import com.example.lee.spotflickr.Gallery.GalleryAdapter;
import com.example.lee.spotflickr.Gallery.Image;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_gallery);

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

        for(String s : url) {
            imgs.add(ImageFromUrl(s));
        }
        galleryAdapter.notifyDataSetChanged();

        // register intent for normal gallery -> custom gallery copy intent
        btnSaveHotspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: naming prompt
                    //TODO: move to hotspot list selection with name, longitude, latitude parameter.
            }
        });
    }

    private Image ImageFromUrl(final String urlString) {
        Image i;
        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch(IOException e) {

                }
            }
        };
        th.start();
        try {
            th.join();
            i = new Image(getFileNameFromUrlString(urlString), bitmap);
            return i;
        } catch(InterruptedException e) {
            return null;
        }
    }
    public static String getFileNameFromUrlString(String urlString) {
        return urlString.substring(urlString.lastIndexOf('/') + 1);
    }
}
