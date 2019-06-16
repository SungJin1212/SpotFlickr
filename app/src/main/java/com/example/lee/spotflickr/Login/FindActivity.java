package com.example.lee.spotflickr.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lee.spotflickr.PopUps.PopUp;
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

    public void showNoticeDialog(String title, String message) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = PopUp.newInstance(title, message);
        dialog.show(getSupportFragmentManager(), "LoginFragment");
    }

    @Override
    public void onClick(View view) {
        if (view == btnFind) {
            String emailAddress = editTextUserEmail.getText().toString().trim();

            if (TextUtils.isEmpty(emailAddress)) {
                showNoticeDialog("Finding Password Error", "Please enter your current password");
                return;
            }
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            //비밀번호 재설정 이메일 보내기



            firebaseAuth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(FindActivity.this, "Success send email.", Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            } else {
                                Toast.makeText(FindActivity.this, "Incorrect email.", Toast.LENGTH_LONG).show();
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
