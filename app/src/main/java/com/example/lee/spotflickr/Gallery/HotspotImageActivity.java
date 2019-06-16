package com.example.lee.spotflickr.Gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lee.spotflickr.DatabaseClasses.HotspotPhoto;
import com.example.lee.spotflickr.Login.LoginActivity;
import com.example.lee.spotflickr.R;
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

import java.util.Iterator;

// ImageActivity for image display
/*
 */
public class HotspotImageActivity extends AppCompatActivity {

    // firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    DatabaseReference mDatabase;
    StorageReference storageRef;
    String storageRefKey;
    String Ref;

    private String filename;
    private double hotspotLongitude;
    private double hotspotLatitude;

    private void setFirebase() {
        //initializig firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        if (firebaseUser == null) {
            //이미 로그인 되었다면 이 액티비티를 종료함
            finish();
            //그리고 profile 액티비티를 연다.
            startActivity(new Intent(getApplicationContext(), LoginActivity.class)); //추가해 줄 ProfileActivity
        }
        mDatabase = FirebaseDatabase.getInstance().getReference(Ref);
        storageRef = firebaseStorage.getReference(storageRefKey);
        if(mDatabase==null) {
            Toast.makeText(HotspotImageActivity.this, "Such Image Not Exists.", Toast.LENGTH_LONG).show();
            finish();
        }
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                filename = dataSnapshot.child("filename").getValue(String.class);
                hotspotLongitude = dataSnapshot.child("longitude").getValue(Double.class);
                hotspotLatitude = dataSnapshot.child("latitude").getValue(Double.class);
                storageRef.getBytes(1024*1024*500).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Image i = new Image(filename, bitmap);
                        ImageView myImage = (ImageView) findViewById(R.id.imageView);
                        myImage.setImageBitmap(i.getImg());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        setFirebase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        storageRefKey = extras.getString("storageRefKey");
        Ref = extras.getString("Ref");

        setContentView(R.layout.activity_hotspot_image);
        init();
    }

}
