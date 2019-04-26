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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.lee.spotflickr.DatabaseClasses.User;

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
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userRef;
    User userObj;
    String userID;

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
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference("User");
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
        final String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        final String nickname = etNickname.getText().toString().trim();
        //email과 password가 비었는지 아닌지를 체크 한다.
        if (TextUtils.isEmpty(email) || !(email.contains("@"))) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password) || (password.length() < 6)) {
            Toast.makeText(this, "Please enter a password longer than 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(this, "Please enter a nickname", Toast.LENGTH_SHORT).show();
            return;
        }

        //email과 password가 제대로 입력되어 있다면 계속 진행된다.
        progressDialog.setMessage("Registering, please wait...");
        progressDialog.show();


        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            try{
                                userID = firebaseAuth.getCurrentUser().getUid();
                            }
                            catch(Exception e){
                                userID = "";
                                toastMessage("No user ID created");
                            }
                            //finish();
                            RegisterNickname(nickname);
                            userObj = new User();
                            userObj.setEmail(email);
                            userObj.setNickname(nickname);
                            userObj.setValidated(false);
                            userRef.child(userID).setValue(userObj).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    toastMessage("Successful registration! Please verify account by clicking on the link in the email sent to " + email);
                                                    Log.d("Debug","Success");
                                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                }
                                                else {
                                                    toastMessage(task.getException().getMessage());
                                                }
                                            }
                                        });

                                    }
                                    else{
                                        toastMessage(task.getException().getMessage());
                                    }
                                }
                            });
                        } else {
                            //에러발생시
                            tvMessage.setText("Registration Error!\nEmail is already in use.");
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

    public void toastMessage(String message){
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}