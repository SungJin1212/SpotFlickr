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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordChangeActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseUser user;
    Intent intent;
    Button btnConfirm;
    EditText etNewpassword;
    EditText etNewpasswordconfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);

        btnConfirm = findViewById(R.id.buttonConfirm);
        btnConfirm.setOnClickListener(this);
        etNewpassword = findViewById(R.id.editTextNewPassword);
        etNewpasswordconfirm = findViewById(R.id.editTextNewPasswordConfirm);
        intent = new Intent(this, LoginActivity.class);

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
            String NewPassword = etNewpassword.getText().toString().trim();
            String NewPasswordconfirm = etNewpasswordconfirm.getText().toString().trim();

//            Log.d("디버그",NewPassworad);
//            Log.d("디버그",NewPasswordconfirm);

            if (NewPassword.equals(NewPasswordconfirm)) {
                user.updatePassword(NewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(intent);
                            finish();
                        }
                    }
                });

            }
        }
    }
}