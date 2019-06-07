package com.example.lee.spotflickr.Gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.lee.spotflickr.R;

public class TakenPhotoActivity  extends AppCompatActivity implements View.OnClickListener {

    public Button uploadBtn;
    public ImageView imageV;
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_taken_photo);
        setUI();
    }

    public void setUI(){
        uploadBtn = findViewById(R.id.buttonUpload);
        uploadBtn.setOnClickListener(this);
        imageV = findViewById(R.id.takenPhoto);
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        imageV.setImageBitmap(image);
    }

    public void uploadToFlickr(){
        Uri webpage = Uri.parse("https://www.flickr.com/photos/upload/");
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
        if (webIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(webIntent);
        }

    }

    @Override
    public void onClick(View v) {
        uploadToFlickr();
    }
}
