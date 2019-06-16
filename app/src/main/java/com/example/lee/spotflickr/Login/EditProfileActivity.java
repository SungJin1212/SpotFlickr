package com.example.lee.spotflickr.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.lee.spotflickr.PopUps.DeleteAccountDialogFragment;
import com.example.lee.spotflickr.PopUps.PopUp;
import com.example.lee.spotflickr.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;



public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, DeleteAccountDialogFragment.NoticeDialogListener {
    AuthCredential credential;
    FirebaseAuth myAuth;
    EditText newPassword;
    EditText repeatNewPassword;
    EditText currentPassword;
    EditText verifyPassword;
    Button changePassword;
    Button deleteAccount;
    Button backButton;
    String email;
    FirebaseUser user;
    FirebaseDatabase myDB;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        myAuth = FirebaseAuth.getInstance();
        init();
    }

    public void init(){
        setUI();
        setFirebase();
        setButton();
    }

    public void setUI(){
        myAuth = FirebaseAuth.getInstance();
        newPassword = findViewById(R.id.newpass);
        repeatNewPassword = findViewById(R.id.repeatnewpass);
        currentPassword = findViewById(R.id.currentpass);
        verifyPassword = findViewById(R.id.verifiedPassword);
        changePassword = findViewById(R.id.changepass);
        deleteAccount = findViewById(R.id.delete);
        backButton = findViewById(R.id.back);
    }

    public void setFirebase(){
        myDB = FirebaseDatabase.getInstance();
        //initializig firebase auth object
        myAuth = FirebaseAuth.getInstance();
        user = myAuth.getCurrentUser();
        if (user != null) {
            // User is already logged in
            email = user.getEmail();
            myRef = myDB.getReference("User").child(user.getUid());
        }
    }

    public void setButton(){
        changePassword.setOnClickListener(this);
        deleteAccount.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    public void showNoticeDialog(String title, String message) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = PopUp.newInstance(title, message);
        dialog.show(getSupportFragmentManager(), "LoginFragment");
    }


    public void updatePassword(){

        String npassword = newPassword.getText().toString().trim();
        String repassword = repeatNewPassword.getText().toString().trim();
        String curpassword = currentPassword.getText().toString().trim();

        if (TextUtils.isEmpty(npassword)) {
            showNoticeDialog("Change account Error", "Please enter your new password");
            return;
        }
        if (TextUtils.isEmpty(repassword)) {
            showNoticeDialog("Change account Error", "Please enter your repeat new password");
            return;
        }
        if (TextUtils.isEmpty(curpassword)) {
            showNoticeDialog("Change account Error", "Please enter your correct current password");
            return;
        }



        if (newPassword.getText().toString().trim().equals(repeatNewPassword.getText().toString().trim())) {
            credential = EmailAuthProvider.getCredential(email, currentPassword.getText().toString().trim());
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    showPopUpNoticeDialog("Success", "Password successfully updated");
                                } else {
                                    showPopUpNoticeDialog("Error", "Error when updating password");
                                }
                            }
                        });
                    } else {
                        showPopUpNoticeDialog("Error", "Incorrect current password was entered");
                    }
                }
            });
        } else {
            showPopUpNoticeDialog("Error", "New passwords do not match");
        }
    }

    public void deleteUserAccount(){
        String password = verifyPassword.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            showNoticeDialog("Delete account Error", "Please enter your correct password");
            return;
        }

        credential = EmailAuthProvider.getCredential(email, verifyPassword.getText().toString().trim());
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    showNoticeDialog();
                }
                else {
                    showPopUpNoticeDialog("Error", "Incorrect password was entered");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        if (view == changePassword) {
            updatePassword();
        }
        if (view == deleteAccount) {
            deleteUserAccount();
        }
        if (view == backButton) {
            finish();
        }
    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new DeleteAccountDialogFragment();
        dialog.show(getSupportFragmentManager(), "DeleteAccountFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        try{
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        showPopUpNoticeDialog("Account Details", "Account has been deleted");
                        myRef.removeValue();
                        // TODO Delete all related data; hotspotlists, photos etc.
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                    else {
                        showPopUpNoticeDialog("Error", "Something went wrong when trying to delete account.");
                    }
                }
            });
        } catch (Exception e){
            showPopUpNoticeDialog("Error", "Account could not be deleted");
        }
    }


    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        verifyPassword.setText("");
    }

    public void showPopUpNoticeDialog(String title, String message) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = PopUp.newInstance(title, message);
        dialog.show(getSupportFragmentManager(), "EditProfilenFragment");
    }

}
