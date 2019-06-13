package com.example.lee.spotflickr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lee.spotflickr.DatabaseClasses.Hotspot;
import com.example.lee.spotflickr.DatabaseClasses.HotspotList;
import com.example.lee.spotflickr.Gallery.HotspotActivity;
import com.example.lee.spotflickr.Login.LoginActivity;
import com.example.lee.spotflickr.PopUps.AddHotspotDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FlickrHotspotListActivity extends AppCompatActivity implements AddHotspotDialogFragment.NoticeDialogListener{
    ArrayList<String> items;
    ArrayList<String> itemKeys;
    ArrayAdapter<String> adapter;
    ListView listview;

    // firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference mDatabase;

    // target hotspot parameter
    String hotspotName;
    double longitude;
    double latitude;

    // button
    Button btnAddToCheckedList;

    ArrayList<String> keyLists;
    boolean eventualExists;

    private void setBtnlistener() {
        btnAddToCheckedList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt = getCheckCnt();
                if(cnt==0) {
                    Toast.makeText(FlickrHotspotListActivity.this, "Nothing is selected.", Toast.LENGTH_LONG).show();
                } else {
                    keyLists = getCheckedItemKeys();
                    showNoticeDialog();
                    clearCheck();
                }
            }
        });
    }

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
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(mDatabase==null) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }
    private void syncHotspotList() {
        mDatabase.child("HotspotList").orderByChild("userEmail").equalTo(firebaseUser.getEmail()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                itemKeys.clear();
                if(dataSnapshot.getChildrenCount()==0) {
                    tryAddWithName("Favorites");
                } else {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        HotspotList hl = userSnapshot.getValue(HotspotList.class);
                        items.add(hl.getName());
                        itemKeys.add(userSnapshot.getKey());
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FlickrHotspotListActivity.this, "Error occur.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_hotspot_list);

        btnAddToCheckedList = findViewById(R.id.addToCheckedList);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        hotspotName = extras.getString("hotspotName");
        longitude = extras.getDouble("longitude");
        latitude = extras.getDouble("latitude");

        Log.d("Debug", "HJ Debug::"+hotspotName);

        // listview create, adapter setting
        keyLists = new ArrayList<String>();
        items = new ArrayList<String>();
        itemKeys = new ArrayList<String>();
        listview = (ListView) findViewById(R.id.hotspotListView);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setItemsCanFocus(false);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listview.setAdapter(adapter);

        setListViewListener();
        setBtnlistener();

        setFirebase();
        syncHotspotList();
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
    private ArrayList<String> getCheckedItems() {
        ArrayList<String> res = new ArrayList<String>();
        for (int i=0; i<items.size(); i++) {
            if(listview.isItemChecked(i)) {
                res.add(items.get(i));
            }
        }
        return res;
    }
    private ArrayList<String> getCheckedItemKeys() {
        ArrayList<String> res = new ArrayList<String>();
        for (int i=0; i<items.size(); i++) {
            if(listview.isItemChecked(i)) {
                res.add(itemKeys.get(i));
            }
        }
        return res;
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

    private void setListViewListener() {
        final Context c = this;
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                keyLists.clear();
                keyLists.add(itemKeys.get(position));
                showNoticeDialog();
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

    private void tryAddWithName(String name) {
        for(String nm: items) {
            if(nm.equals(name)) {
                Toast.makeText(this, "Same hotspot list name already exists.", Toast.LENGTH_LONG).show();
                return;
            }
        }
        HotspotList hl = new HotspotList(name, firebaseUser.getEmail(), null);
        mDatabase.child("HotspotList").push().setValue(hl);
    }

    private void tryAddHotspot() {
        ArrayList<String> keys = keyLists;
        final Hotspot h = new Hotspot(hotspotName, longitude, latitude, null);
        eventualExists=false;
        for(final String k: keys) {
            mDatabase.child("HotspotList").child(k).child("hotspots").orderByChild("name").equalTo(hotspotName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean existFlag=false;
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        existFlag=true;
                        eventualExists=true;
                        break;
                    }
                    if(!existFlag) {
                        mDatabase.child("HotspotList").child(k).child("hotspots").push().setValue(h);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        Toast.makeText(getApplicationContext(), "hotspot is added successfully.", Toast.LENGTH_LONG).show();
        finish();
        return;
    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new AddHotspotDialogFragment();
        dialog.show(getSupportFragmentManager(), "AddHotspotFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        try{
            tryAddHotspot();
        } catch (Exception e){
            toastMessage("Error on Hotspot Add");
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {   }

    public void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}