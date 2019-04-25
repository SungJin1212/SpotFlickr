package com.example.lee.spotflickr.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.lee.spotflickr.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

class ReautheActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnBack;
    Button btnConfirm;
    EditText etPassword;
    FirebaseUser user;
    AuthCredential credential;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reauth);

        btnBack = findViewById(R.id.back);
        btnBack.setOnClickListener(this);
        btnConfirm = findViewById(R.id.buttonConfirm);
        btnConfirm.setOnClickListener(this);
        etPassword = findViewById(R.id.editTextPassword);
        intent = new Intent(this, PasswordChangeActivity.class);
        init();
    }

    private void init() {
        setFirebase();
    }

    private void setFirebase() {
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onClick(View v) {
        if (v == btnConfirm) {
            String currentPassword = etPassword.getText().toString().trim();
            credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
//                    Log.d("디버그","재인증 성공");
                    startActivity(intent);
                    finish();
                }
            });
        }
        if (v == btnBack) {
            onBackPressed();
        }
    }
}