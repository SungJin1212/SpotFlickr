package com.example.lee.spotflickr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;


public class PhotoListActivity extends AppCompatActivity {
    TextView textView;
    ArrayList<String> url = new ArrayList<String>();
    String temp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photolistactivity);
        init();
    }

    private void init(){
        textView = findViewById(R.id.dummyTextView);
        Intent intent = getIntent();
        url = intent.getStringArrayListExtra("Url");
        for(String s : url) {
            temp += s;
        }
        textView.setText(temp);

    }



}
