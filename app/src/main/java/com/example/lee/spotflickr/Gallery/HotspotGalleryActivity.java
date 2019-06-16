package com.example.lee.spotflickr.Gallery;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
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
import com.example.lee.spotflickr.MainActivity;
import com.example.lee.spotflickr.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
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
    String storageRefKey;
    String Ref;
    ArrayList<HotspotPhoto> pendingPhotos;
    ArrayList<Image> imgs;
    double hotspotLongitude;
    double hotspotLatitude;

    private void setGVEvent() {

        gvGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, HotspotImageActivity.class);
                Bundle extras = new Bundle();
                String filename = imgs.get(position).getFilename();
                int iend = filename.indexOf('.');
                if(iend != -1)
                    filename = filename.substring(0, iend);
                extras.putString("Ref",Ref+"/photos/"+filename);
                extras.putString("storageRefKey",storageRefKey+"/"+filename);
                intent.putExtras(extras);
                // clean up all image to basic
                galleryAdapter.clearChecks();
                startActivity(intent);
            }
        });
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
        if (firebaseUser == null) {
            //이미 로그인 되었다면 이 액티비티를 종료함
            finish();
            //그리고 profile 액티비티를 연다.
            startActivity(new Intent(getApplicationContext(), LoginActivity.class)); //추가해 줄 ProfileActivity
        }
        mDatabase = FirebaseDatabase.getInstance().getReference(Ref);
        storageRef = firebaseStorage.getReference(storageRefKey);
        if(mDatabase==null) {
            Toast.makeText(HotspotGalleryActivity.this, "Such Hotspots Not Exists.", Toast.LENGTH_LONG).show();
            finish();
        }
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hotspotLongitude = dataSnapshot.child("longitude").getValue(Double.class);
                hotspotLatitude = dataSnapshot.child("latitude").getValue(Double.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                    Log.d("HJ Debug", Ref+"thumb"+pt.getFilename());
                    Boolean found=false;
                    for(Image img: imgs) {
                        if(img.getExists()) {
                            continue;
                        } else if(pt.getFilename().equals(img.getFilename())) {
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
                    String filename = pt.getFilename();
                    int iend = filename.indexOf('.');
                    if(iend != -1)
                        filename = filename.substring(0, iend);
                    storageRef.child("thumb"+filename).getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            Image i = new Image(pt.getFilename(), bitmap);
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
        setContentView(R.layout.activity_hotspot_gallery);

        // load parameter from other activities
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        Ref = extras.getString("Ref");
        storageRefKey = extras.getString("storageRef");

        // setup objects
        btnLoadLocal = findViewById(R.id.btnLoadLocal);
        btnRemove = findViewById(R.id.btnRemove);
        gvGallery = (GridView)findViewById(R.id.gv);// activity_gallery.xml에서 선언한 Gallery를 연결

        imgs = new ArrayList<Image>();
        pendingPhotos = new ArrayList<HotspotPhoto>();
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

                // TODO: request image remove on firebase
                //    TODO: First it removes data from storage.
                //    TODO: Second it removes data from hotspot.
                ArrayList<Image> imgs1 = galleryAdapter.popCheckedImage();
                if(imgs1.size()!=1) {
                    Toast.makeText(getApplicationContext(), "Please select one image for deletion..", Toast.LENGTH_LONG).show();
                }
                for(final Image iv: imgs1) {
                    String filename = iv.getFilename();
                    int iend = filename.indexOf('.');
                    if(iend != -1)
                        filename = filename.substring(0, iend);
                    final String filename0 = filename;
                    Task initTask = mDatabase.child("photos").child(filename0).setValue(null);

                    initTask.addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            storageRef.child("thumb"+filename0).delete();
                            storageRef.child(filename0).delete();
                            imgs.remove(iv);
                            galleryAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Deletion Success..", Toast.LENGTH_LONG).show();
                        }
                    });

                    initTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Deletion Failed..", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        setGVEvent();
    }

    public Uri thumbnailURIFromOriginalURI(Uri selectedImageUri) {
        long rowId = Long.valueOf(selectedImageUri.getLastPathSegment());
        return uriToThumbnail(""+ rowId);
    }
    public Uri uriToThumbnail(String imageId) {
        String[] projection = { MediaStore.Images.Thumbnails.DATA };
        ContentResolver contentResolver = getContentResolver();
        Cursor thumbnailCursor = contentResolver.query( MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.Thumbnails.IMAGE_ID + "=?", new String[]{imageId}, null);
        if (thumbnailCursor == null) {
            return null;
        }
        else if (thumbnailCursor.moveToFirst()) {
            int thumbnailColumnIndex = thumbnailCursor.getColumnIndex(projection[0]);
            String thumbnailPath = thumbnailCursor.getString(thumbnailColumnIndex);
            thumbnailCursor.close(); return Uri.parse(thumbnailPath);
        }
        else {
            MediaStore.Images.Thumbnails.getThumbnail(contentResolver, Long.parseLong(imageId), MediaStore.Images.Thumbnails.MINI_KIND, null);
            thumbnailCursor.close();
            return uriToThumbnail(imageId);
        }
    }


    private void uploadToFirebaseUri(final Uri uri) {
        String filename0 = getUriFileName(uri);
        int iend = filename0.indexOf('.');
        if(iend != -1)
            filename0 = filename0.substring(0, iend);
        final String filename = filename0;
        final String origFilename = getUriFileName(uri);
        mDatabase.child("photos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(filename)==false) {
                    UploadTask uploadTask = storageRef.child(filename).putFile(uri);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(getApplicationContext(), "Error occured..", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...

                            //Uri thumbnailUri = thumbnailURIFromOriginalURI(uri);
                            Uri thumbnailUri = uri;
                            if(thumbnailUri==null)
                                Log.d("HJ Debug", "thumbnail is null.");
                            UploadTask uploadTask2 = storageRef.child("thumb"+filename).putFile(thumbnailUri);
                            uploadTask2.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    Toast.makeText(getApplicationContext(), "Error occured on thumbnail..", Toast.LENGTH_LONG).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Log.d("HJ Debug", "thumbnail added.");
                                    HotspotPhoto hp = new HotspotPhoto(origFilename, hotspotLongitude, hotspotLatitude);
                                    mDatabase.child("photos").child(filename).setValue(hp);
                                }
                            });

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Already have file with same name..", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

                    uploadToFirebaseUri(mImageUri);
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();

                            uploadToFirebaseUri(uri);
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

    public String getUriFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}