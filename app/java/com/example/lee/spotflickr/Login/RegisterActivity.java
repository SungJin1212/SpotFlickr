package com.example.lee.spotflickr.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lee.spotflickr.MainActivity;
import com.example.lee.spotflickr.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    //define view objects
    EditText etNickname;
    EditText etEmail;
    EditText etPassword;
    Button btnSignup;
    Button btnBack;
    TextView tvSignin;
    TextView tvMessage;
    ProgressDialog progressDialog;
    //define firebase object
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_register);
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

        if (firebaseAuth.getCurrentUser() != null) {
            //이미 로그인 되었다면 이 액티비티를 종료함
            finish();
            //그리고 profile 액티비티를 연다.
            startActivity(new Intent(getApplicationContext(), MainActivity.class)); //추가해 줄 ProfileActivity
        }
    }

    private void setUI() {
        //initializing views
        etNickname = findViewById(R.id.editTextNickname);
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        tvSignin = findViewById(R.id.textViewSignin);
        tvMessage = findViewById(R.id.textviewMessage);
        btnSignup = findViewById(R.id.buttonSignup);
        btnBack = findViewById(R.id.buttonBack);
        progressDialog = new ProgressDialog(this);
    }

    private void setButton() {
        //button click event
        btnBack.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        tvSignin.setOnClickListener(this);
    }

    //Firebse creating a new user
    private void registerUser() {
        //사용자가 입력하는 email, password를 가져온다.
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        final String nickname = etNickname.getText().toString().trim();
        //email과 password가 비었는지 아닌지를 체크 한다.
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(this, "Nickname을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        //email과 password가 제대로 입력되어 있다면 계속 진행된다.
        progressDialog.setMessage("등록중입니다. 기다려 주세요...");
        progressDialog.show();


        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //finish();
                            Log.d("디버그","성공");
                            RegisterNickname(nickname); // 유저 닉네임 등록
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            //에러발생시
                            tvMessage.setText("에러유형\n - 이미 등록된 이메일  \n -암호 최소 6자리 이상 \n - 서버에러");
                            Toast.makeText(RegisterActivity.this, "Register Error!", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });


    }

    private void RegisterNickname(String name) { //닉네임 기본 프로필 사진 등록
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(Uri.parse("http://mblogthumb2.phinf.naver.net/20150427_261/ninevincent_1430122791768m7oO1_JPEG/kakao_1.jpg?type=w2")) //기본 프로필 사진 등록.
                .build();
        user.updateProfile(profileUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });
    }

    //button click event
    @Override
    public void onClick(View view) {

        if (view == btnBack) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        if (view == btnSignup) {
            //TODO
            registerUser();
        }
        if (view == tvSignin) {
            //TODO
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}