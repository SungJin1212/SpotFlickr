package com.example.lee.spotflickr.Gallery;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lee.spotflickr.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class HotspotListActivity extends AppCompatActivity {
    ArrayList<String> items = new ArrayList<String>();
    ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);
    ListView listview;
    // buttons
    Button addButton;
    Button renameButton;
    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HJ Debug", "TEST");

        // listview create, adapter setting
        listview = (ListView) findViewById(R.id.listView);
        listview.setAdapter(adapter);
        // button setting
        addButton = (Button)findViewById(R.id.add);
        renameButton = (Button)findViewById(R.id.rename);
        deleteButton = (Button)findViewById(R.id.delete);

        //todo:: request firebase about lists, load as view.
    }
    private void setBtnlistener() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDialog(0, "Add Hotspotlist", "Please Name Hotspot list.");
            }
        });
        renameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDialog(1, "Add Hotspotlist", "Please Name Hotspot list.");
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void textDialog(final int actionType, String title, String content) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(title);
        alert.setMessage(content);

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                if(actionType==0) {
                    tryAddWithName(value);
                } else {
                    tryRenameWithName(value);
                }
            }
        });
        alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Canceled.
            }
        });
        alert.show();
    }
    private void tryAddWithName(String name) {
        // request add list on firebase

        // if failed, make message

        // if success, sync with local.

    }
    private void tryRenameWithName(String name) {
        // request renanme list on firebase

        // if failed, make message

        // if success, sync with local.
    }
}