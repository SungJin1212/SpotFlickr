package com.example.lee.spotflickr.Gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lee.spotflickr.R;

import java.io.File;

// ImageActivity for image display
/*
    TODO: get parameter from GalleryActivity, or map
        TODO: if parameter related to local storage, get image data and show it.
            TODO: show image and sync firebase. If such file not exists in local storage, request it to firebase and save it to the user storage
            TODO: delete button: delete image from firebase and user storage
            TODO: upload to flickr button: try upload to flickr
        TODO: if parameter related to flickr, request image data and show it.
            TODO: download button: download image related data to firebase and user storage
 */
public class ImageActivity extends AppCompatActivity implements View.OnClickListener {
    // Define U and F for UserImage and FlickrImage processing purpose
    private static final int U = 0;
    private static final int F = 1;

    private Button btnData;

    private int purpose;

    private void setUI() {
        //initializing views
        btnData = (Button) findViewById(R.id.buttonData);
    }

    private void setButton() {
        //button click event
        btnData.setOnClickListener(this);
    }

    private void init() {
        setUI();
        setButton();
    }

    @Override
    public void onClick(View view) {
        if (view == btnData) {
            if(purpose == U) {
                // TODO: upload to flickr
                Toast.makeText(getApplicationContext(), "DEBUG: upload to flickr button clicked", Toast.LENGTH_LONG).show();
            } else {
                // TODO: download from flickr
                Toast.makeText(getApplicationContext(), "DEBUG: download from flickr button clicked", Toast.LENGTH_LONG).show();
            }
        }
    }
/*
    private boolean checkDisplayLocal(String query) {
        Image img = new Image(query, false);
        if(img.getImg()==null)
            return false;
        ImageView myImage = (ImageView) findViewById(R.id.imageView);
        myImage.setImageBitmap(img.getImg());
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String fromActivity = extras.getString("Purpose");
        String query = extras.getString("Query");
        Log.d("Debug", "HJ Debug: "+fromActivity+query);

        if(fromActivity.equals("U")) {   // User Image Processing
            purpose = U;
            setContentView(R.layout.activity_user_image);
            init();
            if(! checkDisplayLocal(query)) {
                // TODO: request to firebase
            }
        } else if(fromActivity.equals("F")) {    // Flickr Image Processing
            purpose = F;
            setContentView(R.layout.activity_flickr_image);
            init();
            // TODO: request to flickr
        }
    }
    */
}
