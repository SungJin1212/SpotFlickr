package com.example.lee.spotflickr.Gallery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.example.lee.spotflickr.Login.LoginActivity;
import com.example.lee.spotflickr.MainActivity;
import com.example.lee.spotflickr.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HotspotActivity extends AppCompatActivity {
    ArrayList<String> items;
    ArrayList<String> itemKeys;
    ArrayAdapter<String> adapter;
    ListView listview;
    // buttons
    Button addButton;
    Button renameButton;
    Button deleteButton;
    // firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference mDatabase;
    String hotspotListKey;

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
        mDatabase = FirebaseDatabase.getInstance().getReference("HotspotList").child(hotspotListKey);
        if(mDatabase==null) {
            Toast.makeText(HotspotActivity.this, "Such Hotspots Not Exists.", Toast.LENGTH_LONG).show();
            finish();
        }
    }
    private void syncHotspot() {
        mDatabase.child("hotspots").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                itemKeys.clear();
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    Hotspot hl = userSnapshot.getValue(Hotspot.class);
                    items.add(hl.getName());
                    itemKeys.add(userSnapshot.getKey());
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HotspotActivity.this, "Error occur.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        hotspotListKey = extras.getString("HotspotListKey");

        // listview create, adapter setting
        items = new ArrayList<String>();
        itemKeys = new ArrayList<String>();
        listview = (ListView) findViewById(R.id.hotspotView);
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

        setFirebase();
        syncHotspot();
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
    private void setBtnlistener() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDialog(0, "Add Hotspot", "Please Name Hotspot.");
                clearCheck();
            }
        });
        renameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt = getCheckCnt();
                if(cnt==0) {
                    Toast.makeText(HotspotActivity.this, "Nothing is selected.", Toast.LENGTH_LONG).show();
                } else if(cnt > 1) {
                    Toast.makeText(HotspotActivity.this, "Please select only one list to rename.", Toast.LENGTH_LONG).show();
                    clearCheck();
                } else {
                    textDialog(1, "Rename Hotspot", "Please Name Hotspot.");
                }
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt = getCheckCnt();
                if(cnt==0) {
                    Toast.makeText(HotspotActivity.this, "Nothing is selected.", Toast.LENGTH_LONG).show();
                } else {
                    tryDelete();
                }
            }
        });
    }
    private void setListViewListener() {
        final Context c = this;
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // move to Gallery Adapter
                Intent intent = new Intent(c, HotspotGalleryActivity.class);
                Bundle extras = new Bundle();
                extras.putString("Ref","HotspotList/"+hotspotListKey+"/hotspots/"+itemKeys.get(position));
                extras.putString("storageRef",itemKeys.get(position));
                intent.putExtras(extras);
                // clean up all image to basic
                clearCheck();
                startActivity(intent);
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
                    Toast.makeText(HotspotActivity.this, "Invalid hotspot name.", Toast.LENGTH_LONG).show();
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

    private void tryAddWithName(String name) {
        for(String nm: items) {
            if(nm.equals(name)) {
                Toast.makeText(this, "Same hotspot name already exists.", Toast.LENGTH_LONG).show();
                return;
            }
        }
        Hotspot h = new Hotspot(name, -1.1, -1.1, null);
        mDatabase.child("hotspots").push().setValue(h);
    }
    private void tryRenameWithName(final String name) {
        for(String nm: items) {
            if(nm.equals(name)) {
                Toast.makeText(this, "Same hotspot list name already exists.", Toast.LENGTH_LONG).show();
                return;
            }
        }
        int pos = getCheckedPos();
        Log.d("HJ Debug", ""+pos);
        String target = items.get(pos);

        Log.d("HJ Debug", target);
        mDatabase.child("hotspots").orderByChild("name").equalTo(target).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    String key = userSnapshot.getKey();
                    Log.d("HJ Debug", "key:"+key);
                    mDatabase.child("hotspots").child(key).child("name").setValue(name);
                }
                clearCheck();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                clearCheck();
            }
        });
    }
    private void tryDelete() {
        ArrayList<String> keys = getCheckedItemKeys();
        ArrayList<String> selected_items = getCheckedItems();
        for(int i=0; i<selected_items.size(); i++) {

        }
        for(String k: keys) {
            mDatabase.child("hotspots").child(k).setValue(null);
        }
        clearCheck();
    }
}