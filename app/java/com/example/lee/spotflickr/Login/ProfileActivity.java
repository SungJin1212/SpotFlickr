package com.example.lee.spotflickr.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.lee.spotflickr.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvEmail;
    TextView tvNickname;

    //define firebase object
    private FirebaseUser user;
    private Button btBack;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        tvEmail = findViewById(R.id.textViewEmail);
        tvNickname = findViewById(R.id.textViewNickname);
        btBack = findViewById(R.id.back);
        btBack.setOnClickListener(this);

        btnChangePassword = findViewById(R.id.changePassword);
        btnChangePassword.setOnClickListener(this);

        init();
        ImageView profile = findViewById(R.id.profileImage);
        GlideApp.with(this).load(user.getPhotoUrl()).transform(new CircleCrop()).into(profile);

    }

    private void init() {
        setFirebase();
        setText();
    }

    private void setFirebase() {
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setText() { //유저 정보 표시.
        tvEmail.setText(user.getEmail());
        tvNickname.setText(user.getDisplayName());
    }


    @Override
    public void onClick(View v) {
        if (v == btBack) {
            onBackPressed();
        }
        if (v == btnChangePassword) {
            startActivity(new Intent(this, ReautheActivity.class));
            finish();
        }
    }
}