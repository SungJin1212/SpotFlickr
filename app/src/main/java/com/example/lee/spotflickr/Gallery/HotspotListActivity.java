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
    ArrayList<String> items;
    ArrayAdapter<String> adapter;
    ListView listview;
    // buttons
    Button addButton;
    Button renameButton;
    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot_list);
        Log.d("HJ Debug", "TEST");
        items = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        // listview create, adapter setting
        listview = (ListView) findViewById(R.id.hotspotListView);
        listview.setAdapter(adapter);
        // button setting
        addButton = (Button)findViewById(R.id.add);
        renameButton = (Button)findViewById(R.id.rename);
        deleteButton = (Button)findViewById(R.id.delete);
        setBtnlistener();

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
        Log.d("HJ Debug", "alert showed.");
    }
    private int reqHotspotListAddFirebase(String name) {
        return 0;
    }
    private void tryAddWithName(String name) {
        // request add list on firebase
        int res = reqHotspotListAddFirebase(name);
        // if failed, make message
        switch(res) {
            case -1:
                Toast.makeText(this, "Same hotspot list name already exists.", Toast.LENGTH_LONG).show();
                break;
            case -2:
                Toast.makeText(this, "Unknown Error.", Toast.LENGTH_LONG).show();
                break;
            case 0:
                // if success, sync with local.
                items.add(name);
                adapter.notifyDataSetChanged();
                break;
        }
    }
    private void tryRenameWithName(String name) {
        // request renanme list on firebase

        // if failed, make message

        // if success, sync with local.
    }
}