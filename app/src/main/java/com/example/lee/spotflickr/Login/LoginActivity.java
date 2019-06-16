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
import android.widget.TextView;
import android.widget.Toast;

import com.example.lee.spotflickr.MainActivity;
import com.example.lee.spotflickr.PopUps.PopUp;
import com.example.lee.spotflickr.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //define view objects
    EditText etEmail;
    EditText etPassword;
    Button btnSignin;
    TextView tvSignin;
    TextView tvMessage;
    TextView tvFindpassword;
    ProgressDialog progressDialog;
    //define firebase object
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        setFirebase();
        setUI();
        setButton();
    }

    private void setFirebase() {
        //initializig firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User");
        if (firebaseAuth.getCurrentUser() != null) {
            if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                //이미 로그인 되었다면 이 액티비티를 종료함
                finish();
                //그리고 profile 액티비티를 연다.
                startActivity(new Intent(getApplicationContext(), MainActivity.class)); //추가해 줄 ProfileActivity
            } else {
                showNoticeDialog("Successful registration", "Please verify account by clicking on the link in the email");
                firebaseAuth.signOut();
            }
        }
    }

    private void setUI() {
        //initializing views
        etEmail = (EditText) findViewById(R.id.editTextEmail);
        etPassword = (EditText) findViewById(R.id.editTextPassword);
        tvSignin = (TextView) findViewById(R.id.textViewSignin);
        tvMessage = (TextView) findViewById(R.id.textviewMessage);
        tvFindpassword = (TextView) findViewById(R.id.textViewFindpassword);
        btnSignin = (Button) findViewById(R.id.buttonSignup);
        progressDialog = new ProgressDialog(this);
    }

    private void setButton() {
        //button click event
        btnSignin.setOnClickListener(this);
        tvSignin.setOnClickListener(this);
        tvFindpassword.setOnClickListener(this);
    }

    //firebase userLogin method
    private void userLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            showNoticeDialog("Login Error", "Please enter your email address");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showNoticeDialog("Login Error","Please enter your password");
            return;
        }

        progressDialog.setMessage("Signing in, please wait...");
        progressDialog.show();

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                                myRef.child(firebaseAuth.getCurrentUser().getUid()).child("validated").setValue(true);
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                showNoticeDialog("Login Error",
                                        "You need to verify your account by clicking the link in the email that was sent to you");
                            }
                        } else {
                            showNoticeDialog("Login Failure", "Incorrect Password");
                            tvMessage.setText("Login failure\n - Incorrect password.\n - Server fail");
                        }
                    }
                });
    }

    public void showNoticeDialog(String title, String message) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = PopUp.newInstance(title, message);
        dialog.show(getSupportFragmentManager(), "LoginFragment");
    }


    @Override
    public void onClick(View view) {
        if (view == btnSignin) {
            userLogin();
        }
        if (view == tvSignin) {
            finish();
            startActivity(new Intent(this, RegisterActivity.class));
        }
        if (view == tvFindpassword) {
            finish();
            startActivity(new Intent(this, FindActivity.class));
        }
    }

}
