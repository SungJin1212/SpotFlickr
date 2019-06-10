package com.example.lee.spotflickr.PopUps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class GiveAccessPopUp extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    GiveAccessPopUp.NoticeDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (GiveAccessPopUp.NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Activity must implement NoticeDialogListener");
        }
    }


    public static GiveAccessPopUp newInstance(String title, String message) {
        GiveAccessPopUp myFragment = new GiveAccessPopUp();

        Bundle args = new Bundle();
        args.putString("Title", title);
        args.putString("Message", message);
        myFragment.setArguments(args);

        return myFragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("Title");
        String message = getArguments().getString("Message");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                listener.onDialogPositiveClick(GiveAccessPopUp.this);
                            }
                        }
                )
                .create();
    }
}