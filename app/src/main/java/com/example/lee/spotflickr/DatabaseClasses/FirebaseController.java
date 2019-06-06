package com.example.lee.spotflickr.DatabaseClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class FirebaseController {
    private static FirebaseAuth firebaseAuth;
    private static FirebaseUser firebaseUser;
    private static FirebaseDatabase firebaseDB;
    private static FirebaseStorage firebaseStorage;

    public static void initFirebase() {

    }
    public static void getFirebaseInstance() {
        //initializig firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }
    public static void clearFirebaseInstance() {
        firebaseAuth.signOut();
    }

    // Auth Communication on Context c

    // Database Communication on Context c, image data as b.
    public static void uploadHotspot(Context c, Hotspot h) {

    }
    // Storage Communication on Context c
    // HINT: file.getLastPathSegment() for refName
    public static void attachImgFromName(final Context c, String filepath, String refName) {
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference imageRef = storageRef.child("images/"+refName);

        Uri file = Uri.fromFile(new File(filepath));
        UploadTask uploadTask = imageRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(c, "Upload Failed.", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(c, "Upload Success.", Toast.LENGTH_SHORT).show();

            }
        });
    }
    public static void attachImgFromBitmap(Context c, Bitmap b, String refName) {
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference imageRef = storageRef.child("images/"+refName);
    }
}
