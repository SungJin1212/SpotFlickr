package com.example.lee.spotflickr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lee.spotflickr.Gallery.Image;
import com.example.lee.spotflickr.Login.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FlickrImageActivity extends AppCompatActivity {

    String mUrl;
    Bitmap bitmap;
    Image i;

    private void init() {
        i = ImageFromUrl(mUrl);
        ImageView myImage = (ImageView) findViewById(R.id.imageView);
        myImage.setImageBitmap(i.getImg());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        mUrl = extras.getString("mUrl");

        setContentView(R.layout.activity_flickr_image);
        init();
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
            i.setmUrl(urlString);
            return i;
        } catch(InterruptedException e) {
            return null;
        }
    }
    public static String getFileNameFromUrlString(String urlString) {
        return urlString.substring(urlString.lastIndexOf('/') + 1);
    }
}
