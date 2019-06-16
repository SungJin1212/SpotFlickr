package com.example.lee.spotflickr;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.lee.spotflickr.Gallery.TakenPhotoActivity;
import com.example.lee.spotflickr.Gallery.HotspotListActivity;
import com.example.lee.spotflickr.Login.LoginActivity;
import com.example.lee.spotflickr.Login.ProfileActivity;

import com.example.lee.spotflickr.Map.MapPoint;
import com.example.lee.spotflickr.PopUps.GiveAccessPopUp;
import com.example.lee.spotflickr.retrofit.APIClient;
import com.example.lee.spotflickr.retrofit.parser.Photo;
import com.example.lee.spotflickr.retrofit.parser.PhotoList;
import com.example.lee.spotflickr.retrofit.parser.Photos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, TMapGpsManager.onLocationChangedCallback, GiveAccessPopUp.NoticeDialogListener {
    private boolean started;
    private int counter;
    private String TokenURL;
    private final Context context = this;
    private Gson gson;
    private Button btLogout;
    private Button btHotPlace;
    private Button btMyHotPlace;
    private Button btProfile;
    private ImageButton btCamera;
    private ImageButton btMyLocation;
    private Button btSearch;
    EditText edtSearchText;
    final private static String DEFAULTNAME = "Hotplace";
    private double distance;
    final static double MAXDISTANCE = 200000; // 2km.

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int MY_PERMISSIONS_GET_LOCATION = 2;
    static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 3; // If your app uses the WRITE_EXTERNAL_STORAGE permission, then it implicitly has permission to read the external storage as well.
    static final int MY_PERMISSIONS_CAMERA = 4;
    HashMap<Integer, String> permissions = new HashMap<>();

    // For Custom Routing
    Double RouteLong;
    Double RouteLat;


    FirebaseAuth firebaseAuth;
    FrameLayout Tmap;
    TMapTapi tMapTapi;

    private boolean m_bTrackingMode = true;

    private TMapGpsManager tmapgps = null;
    private TMapView tmapview = null;
    private static String mApiKey = "ddbfd64b-2abc-495a-a89b-99c8e630fd87"; // 발급받은 appKey
    private static int mMarkerID;

    private ArrayList<TMapPoint> m_tmapPoint = new ArrayList<TMapPoint>();
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>();
    private ArrayList<MapPoint> m_mapPoint = new ArrayList<MapPoint>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras==null) {
            RouteLong = -1.0;
            RouteLat = -1.0;
        } else {
            RouteLong = extras.getDouble("RouteLong");
            RouteLat = extras.getDouble("RouteLat");
        }
        Log.d("Debug", "HJ Debug"+RouteLong);
        try {
            Log.d("Debug", "HJ Debug");
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() throws IOException {
        setFirebase();
        setPermissions();
        counter = 0;
        started = false;
        getPermission(permissions.get(2), 2);

        btnSetting();
    }

    private void setPermissions(){
        // Add keys and values (my permission, anrdoid permission)
        permissions.put(MY_PERMISSIONS_GET_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.put(MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.put(MY_PERMISSIONS_CAMERA, Manifest.permission.CAMERA);
    }


    private void getPermission(String currentPermission, int myPermission){

        if (ContextCompat.checkSelfPermission(this, currentPermission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{currentPermission},
                    myPermission);
        } else {
            if (myPermission == 2) {
                if (!started && counter < 2) {
                    mapSetting();
                    started = true;
                }
            } else if (myPermission == 4) {
                takePhoto();
            } else {
                startActivity(new Intent(this, HotspotListActivity.class));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_GET_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    started = true;
                    mapSetting();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        counter ++;
                        showNoticeDialog("Location Access", "Without location access, you will not be able to use this application");
                        if (counter == 2){
                            System.exit(0);
                        }
                    }
                }
                return;
            }

            case MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(this, HotspotListActivity.class));
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showNoticeDialog("External Storage Access", "Without external storage access, you will not be able to use this application");
                    }
                }
                return;
            }
            case MY_PERMISSIONS_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        showNoticeDialog("Camera Access", "Without camera access, you will not be able to take photos using this application");
                    }
                }
            }
        }
    }

    public void showNoticeDialog(String title, String message) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = GiveAccessPopUp.newInstance(title, message);
        dialog.show(getSupportFragmentManager(), "MainFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        getPermission(permissions.get(2), 2);
    }

    private void mapSetting() {
        tMapTapi = new TMapTapi(this);
        Tmap = findViewById(R.id.Tmap);
        tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey(mApiKey);
        Tmap.addView(tmapview);
//        addPoint();
//        showMarkerPoint();
        /* 현재 보는 방향 */
        tmapview.setCompassMode(false);
        /* 현위치 아이콘표시 */
        tmapview.setIconVisibility(true);
        /* 줌레벨 */
        tmapview.setZoomLevel(15);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        setGPS();

        tmapgps = new TMapGpsManager(MainActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(TMapGpsManager.NETWORK_PROVIDER); //연결된 인터넷으로 현 위치를 받습니다.
        //실내일 때 유용합니다.
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER); //gps로 현 위치를 잡습니다.
        tmapgps.OpenGps();

        /*  화면중심을 단말의 현재위치로 이동 */
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);
        tmapview.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
            @Override
            public void onLongPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint) {
                tmapview.removeTMapPath(); // delete polyline.
            }
        });



        // 풍선에서 우측 버튼 클릭시 할 행동입니다
        tmapview.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(final TMapMarkerItem tMapMarkerItem) {

                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Select");
                alertDialogBuilder
                        .setMessage("Select function")
                        .setCancelable(false)
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        // 다이얼로그를 취소한다
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton("Photos",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        final double longitude = tMapMarkerItem.getTMapPoint().getLongitude();
                                        final double latitude = tMapMarkerItem.getTMapPoint().getLatitude();
                                        retrofit2.Call<PhotoList> SearchPhotoCall = APIClient.getInstance().getService().Search_Photo(
                                                "?method=flickr.photos.search&api_key=43e1b76fcd7e86e9d15001d16df34b7a&" + "sort=interestingness-desc&" + "accuracy=1&"+ "lat=" + latitude +
                                                        "&lon=" + longitude + "&radius="+ 0.5 +"&per_page=40&extras=geo%2Curl_s&format=json&nojsoncallback=1");
                                        SearchPhotoCall.enqueue(new Callback<PhotoList>() {
                                            @Override
                                            public void onResponse(Call<PhotoList> call, Response<PhotoList> response) {
                                                Photos Photos = response.body().photos;

                                                ArrayList <String> url = new ArrayList<>();
                                                int cnt=0;
                                                for (Photo photo : Photos.photo) {
                                                    if(cnt>9) {
                                                        break;
                                                    }
                                                    url.add(photo.getUrl_s());
                                                    cnt++;
                                                }
                                                Intent intent = new Intent(MainActivity.this, FlickrGalleryActivity.class);
                                                Bundle extras = new Bundle();
                                                extras.putStringArrayList("Url", url);
                                                extras.putDouble("Longitude", longitude);
                                                extras.putDouble("Latitude", latitude);
                                                intent.putExtras(extras);
                                                startActivityForResult(intent, 1);
                                            }
                                            @Override
                                            public void onFailure(Call<PhotoList> call, Throwable t) {

                                            }
                                        });
                                    }
                                })
                        .setNeutralButton("FindRoute", //findroute.
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        searchRoute(tmapview.getLocationPoint(), tMapMarkerItem.getTMapPoint());
                                    }
                                });

                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();



                //Toast.makeText(MainActivity.this,"클릭",Toast.LENGTH_SHORT).show();
                //tMapTapi.invokeRoute("go",tMapMarkerItem.getPositionX() , tMapMarkerItem.getPositionY());
            }
        });
    }
    private void setGPS() {
        tmapgps = new TMapGpsManager(MainActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(TMapGpsManager.NETWORK_PROVIDER); //연결된 인터넷으로 현 위치를 받습니다.
        //실내일 때 유용합니다.
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER); //gps로 현 위치를 잡습니다.
        tmapgps.OpenGps();
    }


    private void searchRoute(TMapPoint start, TMapPoint end) {
        TMapData data = new TMapData();
        data.findPathData(start, end, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(final TMapPolyLine path) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        path.setLineWidth(5);
                        path.setLineColor(Color.RED);
                        distance = path.getDistance();
                        if (distance >= MAXDISTANCE) {
                            Log.d("디버그", "Too long distance");
                            Toast.makeText(context, "Too long distance!", Toast.LENGTH_SHORT).show();
                        } else {
                            tmapview.addTMapPath(path);
                        }
                        tmapview.addTMapPath(path);
//                        Bitmap s = ((BitmapDrawable)ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_input_delete)).getBitmap();
//                        Bitmap e = ((BitmapDrawable) ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_input_get)).getBitmap();
//                        tmapview.setTMapPathIcon(s, e);

                    }
                });
            }
        });

    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            //현재위치의 좌표를 알수있는 부분
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                tmapview.setLocationPoint(longitude, latitude);
                tmapview.setCenterPoint(longitude, latitude);
                Log.d("TmapTest", "" + longitude + "," + latitude);
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };


    public void addPoint() { //여기에 핀을 꼽을 포인트들을 배열에 add해주세요!
        // 강남 //
        //m_mapPoint.add(new MapPoint("KAIST", 36.366068, 127.363557));
        //m_mapPoint.add(new MapPoint("KAIST", 36.370872, 127.359726));


    }


    public void showMarkerPoint() {// 마커 찍는거 빨간색 포인트.
        if (m_mapPoint.size() == 0) {
            Toast.makeText(this, "There is no hotspot within 10km", Toast.LENGTH_SHORT).show();
        } else {
            tmapview.removeAllMarkerItem(); //remove all marker.
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    //여기에 딜레이 후 시작할 작업들을 입력
                }
            }, 500);// 0.5초 정도 딜레이를 준 후 시작

            for (int i = 0; i < m_mapPoint.size(); i++) {
                TMapPoint point = new TMapPoint(m_mapPoint.get(i).getLatitude(),
                        m_mapPoint.get(i).getLongitude());
                TMapMarkerItem item = new TMapMarkerItem();
                Bitmap bitmap;
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
                //poi_dot은 지도에 꼽을 빨간 핀 이미지입니다

                item.setTMapPoint(point);
                item.setName(m_mapPoint.get(i).getName());
                item.setVisible(TMapMarkerItem.VISIBLE);
                item.setIcon(bitmap);

                // 풍선뷰 안의 항목에 글을 지정합니다.
                item.setCalloutTitle(m_mapPoint.get(i).getName());
                item.setCalloutSubTitle("HotSpot");
                item.setCanShowCallout(true);
                item.setAutoCalloutVisible(true);

                Bitmap rightButtonClick = BitmapFactory.decodeResource(context.getResources(), R.mipmap.rightarrow);
                item.setCalloutRightButtonImage(rightButtonClick);

                String strID = String.format("pmarker%d", mMarkerID++);
                tmapview.addMarkerItem(strID, item);
                mArrayMarkerID.add(strID);

            }
        }
    }

    private void btnSetting() {
        edtSearchText = findViewById(R.id.edtSearchText);
        btSearch = findViewById(R.id.btnSearch);
        btLogout = findViewById(R.id.btnLogout);
        btProfile = findViewById(R.id.btnProfile);
        btLogout = findViewById(R.id.btnLogout);
        btHotPlace = findViewById(R.id.btnhotPlace);
        btCamera = findViewById(R.id.btncamera);
        btMyHotPlace = findViewById(R.id.btnmyHotPlace);
        btMyLocation = findViewById(R.id.btnMyLocation);

        btMyLocation.setOnClickListener(this);
        btProfile.setOnClickListener(this);
        btSearch.setOnClickListener(this);
        btLogout.setOnClickListener(this);
        btHotPlace.setOnClickListener(this);
        btCamera.setOnClickListener(this);
        btMyHotPlace.setOnClickListener(this);

    }

    private void setFirebase() {
        //initializig firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void SearchPhotoCall(double Long, double Lat,int radius,String s) {

        retrofit2.Call<PhotoList> SearchPhotoCall = APIClient.getInstance().getService().Search_Photo(
                "?method=flickr.photos.search&api_key=43e1b76fcd7e86e9d15001d16df34b7a&" + "sort="+ s + "&accuracy=1&"+ "lat=" + Lat +
                        "&lon=" + Long + "&radius="+ radius +"&per_page=40&extras=geo%2Curl_s&format=json&nojsoncallback=1");
        SearchPhotoCall.enqueue(new Callback<PhotoList>() {
            @Override
            public void onResponse(Call<PhotoList> call, Response<PhotoList> response) {
                if (response.isSuccessful()) {


                    Photos Photos = response.body().photos;
                    if (m_mapPoint.size() != 0) {
                        m_mapPoint.clear();
                    }
                    HashSet title = new HashSet();
                    for (Photo photo : Photos.photo) {
                        if ( photo.getTitle().isEmpty()) {
                            photo.setTitle(DEFAULTNAME);
                        }
                        int temp = title.size();
                        float value = photo.getLatitude() * photo.getLongitude();
                        title.add(Math.round(value));
                        if (title.size() > temp) {
                            m_mapPoint.add(new MapPoint(photo.getTitle(), photo.getLatitude(), photo.getLongitude()));
                        }
                        if(title.size() == 10) {
                            break;
                        }
                    }

                    showMarkerPoint();
                }

            }

            @Override
            public void onFailure(Call<PhotoList> call, Throwable t) {

            }
        });


    }

    private void searchLocation(String searchText) {
        TMapData tMapData = new TMapData();

        tMapData.findAllPOI(searchText, new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList<TMapPOIItem> poiItems) {
                double searchLong = poiItems.get(0).getPOIPoint().getLongitude();
                double searchLat = poiItems.get(0).getPOIPoint().getLatitude();
                tmapview.setCenterPoint(searchLong, searchLat);
                tmapview.setLocationPoint(searchLong, searchLat);
                /* 현위치 아이콘표시 */
                tmapview.setIconVisibility(true);
                /* 줌레벨 */
                tmapview.setZoomLevel(15);
                /*  화면중심을 단말의 현재위치로 이동 */
                tmapview.setTrackingMode(true);
                tmapview.setSightVisible(true);
                SearchPhotoCall(searchLong, searchLat,10,"interestingness-desc");
//                    for(int i=0; i< poiItems.size(); i++) {
//                    TMapPOIItem item = poiItems.get(i);
//                    Log.d("디버그",item.getPOIName() +"," + item.getPOIPoint().toString());
//                }
            }
        });
    }

    public void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();

            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Log.d("Main", "onactivityresult started");
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
            byte[] byteArray = bStream.toByteArray();

            Intent intent = new Intent(getApplicationContext(), TakenPhotoActivity.class);
            intent.putExtra("image", byteArray);
            //finish();
            startActivity(intent);

        }
    }

    @Override
    public void onClick(View view) {
        if( view == btMyLocation) {
            /*  화면중심을 단말의 현재위치로 이동 */
            tmapview.setTrackingMode(true);
            tmapview.setSightVisible(true);
            /* 현위치 아이콘표시 */
            tmapview.setIconVisibility(true);
            /* 줌레벨 */
            tmapview.setZoomLevel(15);

        }
        if (view == btLogout) {
            final Intent loginIntent = new Intent(this, LoginActivity.class);
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
            alertDialogBuilder.setTitle("Logout");
            alertDialogBuilder
                    .setMessage("Do you want to logout?")
                    .setCancelable(false)
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    firebaseAuth.signOut();
                                    finish();
                                    startActivity(loginIntent);
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    // 다이얼로그를 취소한다
                                    dialog.cancel();
                                }
                            });
            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        if (view == btHotPlace) { //search hotplace.
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
            alertDialogBuilder.setTitle("Find HotPlace");
            alertDialogBuilder
                    .setMessage("Select what you want order")
                    .setCancelable(false)
                    .setPositiveButton("Interesting",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    SearchPhotoCall(tmapview.getLocationPoint().getLongitude(), tmapview.getLocationPoint().getLatitude(),10,"interestingness-desc");

                                }
                            })
                    .setNeutralButton("Date", //findroute.
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    SearchPhotoCall(tmapview.getLocationPoint().getLongitude(), tmapview.getLocationPoint().getLatitude(),10,"date-posted-desc");
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    // 다이얼로그를 취소한다
                                    dialog.cancel();
                                }
                            });
            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        if (view == btSearch) {
            String searchText = edtSearchText.getText().toString();

            if (TextUtils.isEmpty(searchText)) {
                Toast.makeText(this, "Please input location", Toast.LENGTH_SHORT).show();
                return;
            }
            searchLocation(searchText);
        }
        if (view == btProfile) {
            startActivity(new Intent(this, ProfileActivity.class));
        }

        if (view == btCamera) { //take photo.
            getPermission(permissions.get(4), 4);

        }
        if (view == btMyHotPlace) {
            getPermission(permissions.get(3), 3);

            Log.d("HJ Debug", "hotlist");

        }
    }




    @Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }
}
