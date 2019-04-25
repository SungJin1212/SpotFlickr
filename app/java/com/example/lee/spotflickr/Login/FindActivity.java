package com.example.lee.spotflickr.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lee.spotflickr.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class FindActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "FindActivity";

    //define view objects
    private EditText editTextUserEmail;
    private Button btnFind;
    private Button btnBack;
    private ProgressDialog progressDialog;
    //define firebase object
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        init();
    }

    private void init() {
        setFirebase();
        setUI();
        setButton();
    }

    private void setFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();

    }

    private void setUI() {
        editTextUserEmail = (EditText) findViewById(R.id.editTextUserEmail);
        btnFind = (Button) findViewById(R.id.buttonFind);
        btnBack = findViewById(R.id.back);
        progressDialog = new ProgressDialog(this);
    }

    private void setButton() {
        btnFind.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnFind) {
            progressDialog.setMessage("처리중입니다. 잠시 기다려 주세요...");
            progressDialog.show();
            //비밀번호 재설정 이메일 보내기
            String emailAddress = editTextUserEmail.getText().toString().trim();
            firebaseAuth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(FindActivity.this, "이메일을 보냈습니다.", Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            } else {
                                Toast.makeText(FindActivity.this, "메일 보내기 실패!", Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
        }
        if (view == btnBack) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}