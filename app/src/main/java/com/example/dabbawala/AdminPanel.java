package com.example.dabbawala;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminPanel extends AppCompatActivity {
    EditText txtResName, txtResID, txtResAddress, txtResPass;
    DatabaseReference mDatabaseReference;
    ListView listView;
    Adapter1 adapter1;
    ArrayList<String> lstHotels = new ArrayList<>();
    ArrayList<String> lstHotelsID = new ArrayList<>();
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtResName = findViewById(R.id.txtResName);
        txtResID = findViewById(R.id.txtResID);
        txtResAddress = findViewById(R.id.txtResAddress);
        txtResPass = findViewById(R.id.txtResPass);
        context = this;
        listView = findViewById(R.id.list);
        adapter1 = new Adapter1(this, R.layout.custum_list, lstHotels);
        listView.setAdapter(adapter1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        switch (which) {
                                                            case DialogInterface.BUTTON_POSITIVE:
                                                                //Yes button clicked
                                                                mDatabaseReference.child(Constants.resturant).child(lstHotelsID.get(position)).removeValue();

                                                                break;

                                                            case DialogInterface.BUTTON_NEGATIVE:
                                                                //No button clicked
                                                                break;
                                                        }
                                                    }
                                                };

                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                builder.setMessage("What do you want?").setPositiveButton("Remove", dialogClickListener)
                                                        .setNegativeButton("Cancel", dialogClickListener).show();
                                            }
                                        }
        );
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);
        mDatabaseReference.child(Constants.resturant).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lstHotelsID.clear();
                lstHotels.clear();
            for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                lstHotels.add(dataSnapshot1.child("resname").getValue().toString()+
                        "\nAddress:"+dataSnapshot1.child("resaddress").getValue().toString());
                lstHotelsID.add(dataSnapshot1.getKey());

            }
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    String resname, resid, resaddress, respass;

    public void saveRes(View v) {
        resname = txtResName.getText().toString();
        resid = txtResID.getText().toString();
        resaddress = txtResAddress.getText().toString();
        respass = txtResPass.getText().toString();
        Map map = new HashMap();
        map.put("resname", resname);
        map.put("resid", resid);
        map.put("resaddress", resaddress);
        map.put("respass", respass);
        if (resname.length() > 0) {
            if (resid.length() > 0) {
                if (respass.length() > 0) {
                    mDatabaseReference.child(Constants.resturant).push().setValue(map);
                    Toast.makeText(this, "Resturant created successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please check password", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please check id", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please check name", Toast.LENGTH_SHORT).show();
        }

    }
}
