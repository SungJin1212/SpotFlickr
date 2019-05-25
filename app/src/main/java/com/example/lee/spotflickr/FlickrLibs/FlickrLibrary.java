package com.example.lee.spotflickr.FlickrLibs;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

import com.example.lee.spotflickr.DatabaseClasses.Hotspot;

public class FlickrLibrary {
    // for just testing. load mechanism should be changed.
    String api_key = "43e1b76fcd7e86e9d15001d16df34b7a";

    void sendRestRequest(final String req) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // All your networking logic
                // should be here
                String result;

                try {
                    URL url = new URL(req);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.connect();
                } catch (Exception e){
                    Log.d("Debug","Error");
                }
            }
        });
    }

    // get nearst set of hotspot
    Hotspot[] getNearstHotspots(int longitude, int latitude) {

        return null;
    }
}
