package com.example.lee.spotflickr;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.lee.spotflickr.Gallery.GalleryActivity;
import com.example.lee.spotflickr.Gallery.GalleryAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnGallery;

    private void setUI() {
        //initializing views
        btnGallery = (Button) findViewById(R.id.buttonGallery);
    }

    private void setButton() {
        //button click event
        btnGallery.setOnClickListener(this);
    }

    private void init() {
        setUI();
        setButton();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    @Override
    public void onClick(View view) {
        if (view == btnGallery) {
            userGallery();
        }
    }
    private void userGallery() {
        finish();
        startActivity(new Intent(this, GalleryActivity.class));
    }
}