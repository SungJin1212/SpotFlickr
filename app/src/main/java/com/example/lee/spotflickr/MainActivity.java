package com.example.lee.spotflickr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.lee.spotflickr.Gallery.GalleryActivity;
import com.example.lee.spotflickr.Login.LoginActivity;
import com.example.lee.spotflickr.Map.MapPoint;
import com.example.lee.spotflickr.Oauth.OAuthTools;
import com.example.lee.spotflickr.retrofit.APIClient;
import com.example.lee.spotflickr.retrofit.parser.Photo;
import com.example.lee.spotflickr.retrofit.parser.PhotoList;
import com.example.lee.spotflickr.retrofit.parser.Photos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class MainActivity extends AppCompatActivity implements View.OnClickListener, TMapGpsManager.onLocationChangedCallback {


    private String TokenURL;
    private final Context context = this;
    private Gson gson;
    private Button btLogout;
    private Button btHotPlace;
    private Button btMyHotPlace;
    private ImageButton btCamera;
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

        try {
            Log.d("Debug", "HJ Debug");
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        OAuthTools.getInstance(context);
    }

    private void init() throws IOException {
        btnSetting();
        mapSetting();
        setFirebase();
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

        // 풍선에서 우측 버튼 클릭시 할 행동입니다
        tmapview.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback()
        {


            @Override
            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                //Toast.makeText(MainActivity.this,"클릭",Toast.LENGTH_SHORT).show();
                //tMapTapi.invokeRoute("go",tMapMarkerItem.getPositionX() , tMapMarkerItem.getPositionY());
                searchRoute(tmapview.getLocationPoint(),tMapMarkerItem.getTMapPoint());
            }
        });


    }

    private void searchRoute(TMapPoint start, TMapPoint end){
        TMapData data = new TMapData();
        data.findPathData(start, end, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(final TMapPolyLine path) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        path.setLineWidth(5);
                        path.setLineColor(Color.RED);
                        tmapview.addTMapPath(path);
//                        Bitmap s = ((BitmapDrawable)ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_input_delete)).getBitmap();
//                        Bitmap e = ((BitmapDrawable) ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_input_get)).getBitmap();
//                        tmapview.setTMapPathIcon(s, e);
                          
                    }
                });
            }
        });
    }

    public void addPoint() { //여기에 핀을 꼽을 포인트들을 배열에 add해주세요!
        // 강남 //
        //m_mapPoint.add(new MapPoint("KAIST", 36.366068, 127.363557));
        //m_mapPoint.add(new MapPoint("KAIST", 36.370872, 127.359726));


    }


    public void showMarkerPoint() {// 마커 찍는거 빨간색 포인트.

        for (int i = 0; i < m_mapPoint.size(); i++) {
            TMapPoint point = new TMapPoint(m_mapPoint.get(i).getLatitude(),
                    m_mapPoint.get(i).getLongitude());
            TMapMarkerItem item = new TMapMarkerItem();
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            //poi_dot은 지도에 꼽을 빨간 핀 이미지입니다

            item.setTMapPoint(point);
            item.setName(m_mapPoint.get(i).getName());
            item.setVisible(item.VISIBLE);

            item.setIcon(bitmap);


            // 풍선뷰 안의 항목에 글을 지정합니다.
            item.setCalloutTitle(m_mapPoint.get(i).getName());
            item.setCalloutSubTitle("hotplace");
            item.setCanShowCallout(true);
            item.setAutoCalloutVisible(true);

            Bitmap rightButtonClick = BitmapFactory.decodeResource(context.getResources(), R.mipmap.rightarrow);

            item.setCalloutRightButtonImage(rightButtonClick);


            String strID = String.format("pmarker%d", mMarkerID++);

            tmapview.addMarkerItem(strID, item);
            mArrayMarkerID.add(strID);

        }
    }

    private void btnSetting() {
        btLogout = findViewById(R.id.btnLogout);
        btHotPlace = findViewById(R.id.btnhotPlace);
        btCamera = findViewById(R.id.btncamera);
        btMyHotPlace = findViewById(R.id.btnmyHotPlace);

        btLogout.setOnClickListener(this);
        btHotPlace.setOnClickListener(this);
        btCamera.setOnClickListener(this);
        btMyHotPlace.setOnClickListener(this);

    }

    private void setFirebase() {
        //initializig firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();


    }
    @Override
    public void onClick(View view) {
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

        if(view == btHotPlace) { //search hotplace.
            SearchPhotoCall();
        }
        if(view == btCamera) { //take photo.
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(intent);
        }
        if(view == btMyHotPlace) {
            startActivity(new Intent(this, GalleryActivity.class));

        }
    }

    @Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    private void SearchPhotoCall() {


        retrofit2.Call<PhotoList> SearchPhotoCall = APIClient.getInstance().getService().Search_Photo(
                "?method=flickr.photos.search&api_key=43e1b76fcd7e86e9d15001d16df34b7a&" + "lat=" + tmapview.getLocationPoint().getLatitude() +
                        "&lon=" + tmapview.getLocationPoint().getLongitude() + "&radius=0.3&extras=geo&format=json&nojsoncallback=1");
        SearchPhotoCall.enqueue(new Callback<PhotoList>() {
            @Override
            public void onResponse(Call<PhotoList> call, Response<PhotoList> response) {
                if(response.isSuccessful()) {
                    Photos Photos = response.body().photos;


                    if(m_mapPoint != null) { //Erase current marker.
                        m_mapPoint.clear();
                    }

                    for(Photo photo : Photos.photo) {
                        m_mapPoint.add(new MapPoint(photo.getTitle(), photo.getLatitude(), photo.getLongitude()));
                    }
                    showMarkerPoint();
                }

            }


            @Override
            public void onFailure(Call<PhotoList> call, Throwable t) {

            }
        });


    }
}
