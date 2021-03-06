package com.example.lee.spotflickr.Gallery;

        import android.content.ContentResolver;
        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.media.ThumbnailUtils;
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
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.GridView;
        import android.widget.Toast;

        import com.example.lee.spotflickr.R;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.util.ArrayList;
        import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private Context context = this;
    private Button btnLoadLocal;
    private Button btnRemove;
    int PICK_IMAGE_MULTIPLE = 1;
    private GridView gvGallery;
    private GalleryAdapter galleryAdapter;

    File currentStorageDir;           // user storage directory
    String[] imgs;


/*
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
    // TODO: consider about conflict control, which happens on same filename
    // from Uri structure, copy a file to currentStorageDir as filename
    public boolean copyFileFromUri(Context context, Uri fileUri, String filename) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            ContentResolver content = context.getContentResolver();
            inputStream = content.openInputStream(fileUri);

            File root = Environment.getExternalStorageDirectory();
            if(root == null) {
                Log.d("debug", "Failed to get root");
            }
            // create a directory
            File saveDirectory = currentStorageDir;
            outputStream = new FileOutputStream( saveDirectory + "/"+filename); // filename.png, .mp3, .mp4 ...
            if(outputStream != null) {
                Log.e( "debug", "Output Stream Opened successfully");
            }

            byte[] buffer = new byte[1000];
            int bytesRead = 0;
            while ( ( bytesRead = inputStream.read( buffer, 0, buffer.length ) ) >= 0 )
            {
                outputStream.write( buffer, 0, buffer.length );
            }
        } catch ( Exception e ){
            Log.e( "debug", "Exception occurred " + e.getMessage());
        } finally{

        }
        return true;
    }
    protected String requestFirebaseUsername() {
        //TODO: request firebase current user's username

        // TODO: replace test return code below.
        return "testUser";
    }
    protected void requestFirebaseUserStorage() {
        //TODO: request firebase user's image storage and update local image storages.
        return;
    }
    protected void uploadFirebaseUserStorage() {
        //TODO: upload image to firebase user's image storage.
        return;
    }

    // refresh grid view based on user storage directory(=currentStorageDir)
    protected boolean loadCustomGallery() {
        File file = currentStorageDir;
        imgs = file.list();
        ArrayList<Image> mArrayImgs = new ArrayList<Image>();
        for(int i=0; i<imgs.length; i++){
            Log.d("Debug","HJ debug success file count-"+i+": "+imgs[i]);
            Image img = new Image(currentStorageDir.getPath()+"/"+imgs[i], true);
            mArrayImgs.add(img);
        }
        galleryAdapter = new GalleryAdapter(getApplicationContext(),mArrayImgs);
        gvGallery.setAdapter(galleryAdapter);
        gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                .getLayoutParams();
        mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

        gvGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: move to ImageActivity
                Log.d("Debug", "HJ Debug: "+currentStorageDir.getPath()+"/"+imgs[position]);

                Intent intent = new Intent(context, ImageActivity.class);
                Bundle extras = new Bundle();
                extras.putString("Purpose","U");   // UserImage(U) or FlickrImage(F)
                extras.putString("Query",currentStorageDir.getPath()+"/"+imgs[position]);
                intent.putExtras(extras);
                // clean up all image to basic
                galleryAdapter.clearChecks();
                startActivity(intent);
            }
        });
        //TODO: multiselect implementation

        gvGallery.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                ((Image)galleryAdapter.getItem(position)).toggleChecked();
                galleryAdapter.notifyDataSetChanged();
                return true;    // the event is consumed.
            }
        });
        return true;
    }

    protected File setRootStorageDir() {
        // App.을 실행하자 마자 지정한 경로의 생성 및 접근에 용이하도록 아래와 같이 생성
        File rootStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SpotFlickrDir");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! rootStorageDir.exists()){
            Log.d("Debug","SpotFlickr Debug: no dir exists, try create...");
            if (! rootStorageDir.mkdirs()){
                Log.d("SpotFlickrDir", "SpotFlickr Debug: failed to create directory");
//                return null;
            }
        } else {
            Log.d("Debug","SpotFlickr Debug: rootStorageExists");
        }
        return rootStorageDir;
    }

    protected File setUserStorageDir(File rootStorageDir, String username) {
        // App.을 실행하자 마자 지정한 경로의 생성 및 접근에 용이하도록 아래와 같이 생성
        File userStorageDir = new File(rootStorageDir.getPath(), username);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! userStorageDir.exists()){
            Log.d("Debug","SpotFlickr Debug: no dir exists, try create...");
            if (! userStorageDir.mkdirs()){
                Log.d("SpotFlickrDir", "SpotFlickr Debug: failed to create directory");
//                return null;
            }
        } else {
            Log.d("Debug","SpotFlickr Debug: userStorageExists");
        }
        return userStorageDir;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // setup objects
        btnLoadLocal = findViewById(R.id.btnLoadLocal);
        btnRemove = findViewById(R.id.btnRemove);
        gvGallery = (GridView)findViewById(R.id.gv);// activity_gallery.xml에서 선언한 Gallery를 연결

        // set root storage for application
        File rootStorage = setRootStorageDir();
        // request firebase username to direct storage for the user
        String username = requestFirebaseUsername();
        currentStorageDir = setUserStorageDir(rootStorage, username);
        // load local gallery view for user to experience less latency, before request data to the firebase
        loadCustomGallery();
        // request firebase to synchronize
        requestFirebaseUserStorage();
        // TODO: Consideration Point 1. what about synchronize firebase asynchronously on background?
        // TODO: For now, we just use synchronous process below.
        // load again after synchronize
        loadCustomGallery();
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


                ArrayList<Image> imgs = galleryAdapter.popCheckedImage();
                for(Image iv: imgs) {
                    File f = new File(iv.getPath());
                    try {
                        if(f.exists()) {
                            f.delete();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Unable to delete file"+iv.getPath(), Toast.LENGTH_LONG).show();
                    }
                }
                loadCustomGallery();
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
                    if(copyFileFromUri(getApplicationContext(), mImageUri, getUriFileName(mImageUri))) {
                        Log.d("Debug","SpotFlickr Debug: file copy success");
                    }
                    loadCustomGallery();
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            if(copyFileFromUri(getApplicationContext(), uri, getUriFileName(uri))) {
                                Log.d("Debug","SpotFlickr Debug: file copy success");
                            }
                            loadCustomGallery();
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
    */
}