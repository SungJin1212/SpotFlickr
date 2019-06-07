package com.example.lee.spotflickr.Gallery;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.lee.spotflickr.DatabaseClasses.Hotspot;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class HotspotGalleryActivity extends AppCompatActivity {
    private Context context = this;
    private Button btnLoadLocal;
    private Button btnRemove;
    int PICK_IMAGE_MULTIPLE = 1;
    private GridView gvGallery;
    private GalleryAdapter galleryAdapter;

    File currentStorageDir;           // user storage directory
    // firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    DatabaseReference mDatabase;
    StorageReference storageRef;
    String Ref;
    ArrayList<HotspotPhoto> pendingPhotos;
    ArrayList<Image> imgs;

    private void setGVEvent() {

        gvGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, HotspotImageActivity.class);
                Bundle extras = new Bundle();
                extras.putString("Ref",Ref+"/photos/"+imgs.get(position).getHash());
                extras.putString("StorageRef",imgs.get(position).getHash());
                intent.putExtras(extras);
                // clean up all image to basic
                galleryAdapter.clearChecks();
                startActivity(intent);
            }
        });
        //multiselect implementation TODO: test required
        gvGallery.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ((Image)galleryAdapter.getItem(position)).toggleChecked();
                galleryAdapter.notifyDataSetChanged();
                return true;    // the event is consumed.
            }
        });
    }


    private void setFirebase() {
        //initializig firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageRef = firebaseStorage.getReference();
        if (firebaseUser == null) {
            //이미 로그인 되었다면 이 액티비티를 종료함
            finish();
            //그리고 profile 액티비티를 연다.
            startActivity(new Intent(getApplicationContext(), LoginActivity.class)); //추가해 줄 ProfileActivity
        }
        mDatabase = FirebaseDatabase.getInstance().getReference(Ref);
        if(mDatabase==null) {
            Toast.makeText(HotspotGalleryActivity.this, "Such Hotspots Not Exists.", Toast.LENGTH_LONG).show();
            finish();
        }
    }
    private void syncImages() {
        mDatabase.child("photos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // load changes
                int passFlag=0;
                for(Image img: imgs) {
                    img.setExists(false);
                }
                pendingPhotos.clear();
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    HotspotPhoto pt = userSnapshot.getValue(HotspotPhoto.class);
                    Boolean found=false;
                    for(Image img: imgs) {
                        if(img.getExists()) {
                            continue;
                        } else if(pt.getStorageHash().equals(img.getHash())) {
                            img.setExists(true);
                            found = true;
                            break;
                        }
                    }
                    if(found==false) {
                        pendingPhotos.add(pt);
                    }
                }
                // remove excluded one
                Iterator<Image> itImgs = imgs.iterator();
                while(itImgs.hasNext()) {
                    Image itImg = itImgs.next();
                    if(itImg.getExists()==false) {
                        itImgs.remove();
                    }
                }
                for(final HotspotPhoto pt: pendingPhotos) {
                    storageRef.child("thumb"+pt.getStorageHash()).getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            Image i = new Image(pt.getFilename(), pt.getStorageHash(), bitmap);
                            imgs.add(i);
                            galleryAdapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HotspotGalleryActivity.this, "Error occur.", Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // load parameter from other activities
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        Ref = extras.getString("Ref");

        // setup objects
        btnLoadLocal = findViewById(R.id.btnLoadLocal);
        btnRemove = findViewById(R.id.btnRemove);
        gvGallery = (GridView)findViewById(R.id.gv);// activity_gallery.xml에서 선언한 Gallery를 연결

        imgs = new ArrayList<Image>();
        galleryAdapter = new GalleryAdapter(getApplicationContext(),imgs);
        gvGallery.setAdapter(galleryAdapter);
        gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                .getLayoutParams();
        mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

        setFirebase();
        syncImages();

        // register intent for normal gallery -> custom gallery copy intent
        btnLoadLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });
        // remove function - remove checked images from user storage and firebase
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Trying to remove images from firebase...", Toast.LENGTH_LONG).show();
                // TODO: request image remove on firebase
                //    TODO: First it removes data from storage.
                //    TODO: Second it removes data from hotspot.
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                if(data.getData()!=null){

                    Uri mImageUri=data.getData();

                    // TODO: load to firebase storage (file name)
                            // TODO: push file with filename->key, add file("thumb"key) with filename.
                    // TODO: load to firebase database (filename, longitude, latitude, key)
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();

                            // TODO: load to firebase storage (file name)
                            // TODO: push file with filename->key, add file("thumb"key) with filename.
                            // TODO: load to firebase database (filename, longitude, latitude, key)
                        }
                        Log.d("Debug","SpotFlickr Debug: Selected Images" + mArrayUri.size());
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}