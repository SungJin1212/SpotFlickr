package com.example.lee.spotflickr.PopUps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class PopUp extends DialogFragment {

    public static PopUp newInstance(String title, String message) {
         PopUp myFragment = new PopUp();

        Bundle args = new Bundle();
        args.putString("Title", title);
        args.putString("Message", message);
        myFragment.setArguments(args);

        return myFragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("Title", "");
        String message = getArguments().getString("Message", "");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //
                            }
                        }
                )
                .create();
    }
}