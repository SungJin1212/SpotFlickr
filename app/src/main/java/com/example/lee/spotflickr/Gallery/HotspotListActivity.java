package com.example.lee.spotflickr.Gallery;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lee.spotflickr.DatabaseClasses.HotspotList;
import com.example.lee.spotflickr.Login.LoginActivity;
import com.example.lee.spotflickr.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    // firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference hotspotListDB;

    private void setFirebase() {
        //initializig firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            //이미 로그인 되었다면 이 액티비티를 종료함
            finish();
            //그리고 profile 액티비티를 연다.
            startActivity(new Intent(getApplicationContext(), LoginActivity.class)); //추가해 줄 ProfileActivity
        }
        database = FirebaseDatabase.getInstance();
        hotspotListDB = database.getReference("Hotspotlist");
    }
    private ArrayList<HotspotList> getHotspotList() {
        hotspotListDB.orderByChild("userEmail").equalTo(firebaseUser.getEmail());
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot_list);



        // listview create, adapter setting
        items = new ArrayList<String>();
        listview = (ListView) findViewById(R.id.hotspotListView);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setItemsCanFocus(false);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listview.setAdapter(adapter);
        // button setting
        addButton = (Button)findViewById(R.id.add);
        renameButton = (Button)findViewById(R.id.rename);
        deleteButton = (Button)findViewById(R.id.delete);
        setBtnlistener();
        setListViewListener();

        //TODO: 1. load 'hotspotlist' of 'user'
        //TODO:    1-1. if no hotspotlist exists, create a hotspotlist with name 'Favorites'
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference hotspotListRef = firebaseDatabase.getReference("User");

    }
    private int getCheckCnt() {
        int cnt = 0;
        for (int i = 0; i < items.size(); i++) {
            if (listview.isItemChecked(i)) {
                cnt++;
            }
        }
        return cnt;
    }
    private int getCheckedPos() {
        for (int i = 0; i < items.size(); i++) {
            if (listview.isItemChecked(i)) {
                return i;
            }
        }
        return -1;
    }
    private void clearCheck() {
        for (int i=0; i<items.size(); i++) {
            if(listview.isItemChecked(i)) {
                listview.setItemChecked(i, false);
                listview.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }
    private void removeChecked() {
        ArrayList<Integer> posList = new ArrayList<Integer>();
        int removed = 0;
        for (int i=0; i<items.size(); i++) {
            if(listview.isItemChecked(i)) {
                posList.add(i);
                listview.setItemChecked(i, false);
                listview.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }
        }
        for (int pos: posList) {
            items.remove(pos-removed);
            removed++;
        }
        adapter.notifyDataSetChanged();
    }
    private void setBtnlistener() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDialog(0, "Add Hotspotlist", "Please Name Hotspot list.");
                clearCheck();
            }
        });
        renameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt = getCheckCnt();
                if(cnt==0) {
                    Toast.makeText(HotspotListActivity.this, "Nothing is selected.", Toast.LENGTH_LONG).show();
                } else if(cnt > 1) {
                    Toast.makeText(HotspotListActivity.this, "Please select only one list to rename.", Toast.LENGTH_LONG).show();
                    clearCheck();
                } else {
                    textDialog(1, "Rename Hotspotlist", "Please Name Hotspot list.");
                }
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt = getCheckCnt();
                if(cnt==0) {
                    Toast.makeText(HotspotListActivity.this, "Nothing is selected.", Toast.LENGTH_LONG).show();
                } else {
                    tryDelete();
                }
            }
        });
    }
    private void setListViewListener() {
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // move to Gallery Adapter
            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // check as selected
                if(listview.isItemChecked(position)) {
                    listview.setItemChecked(position, false);
                    parent.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                } else {
                    listview.setItemChecked(position, true);
                    parent.getChildAt(position).setBackgroundColor(Color.GRAY);
                }
                return true;
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
                if(value.trim().equals("")) {
                    Toast.makeText(HotspotListActivity.this, "Invalid hotspotlist name.", Toast.LENGTH_LONG).show();
                }
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
    private void tryDelete() {
        // request deletion on the list of firebase

        // if failed, make message

        // if success, delete it on display + local
        removeChecked();
    }
}