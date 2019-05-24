package com.example.dabbawala;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewCurrentOrdersUser extends AppCompatActivity {

    Spinner spnType, spnCategory;
    DatabaseReference mDatabaseReference;
    ArrayList<String> lstHotels = new ArrayList<>();
    ArrayList<String> lstOrderID = new ArrayList<>();
    ArrayList<String> lstHistoryStatus = new ArrayList<>();
    ListView listView;
    Adapter1 adapter1;

    Context context;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);


        listView = findViewById(R.id.list);
        adapter1 = new Adapter1(this, R.layout.custum_list, lstHotels);
        listView.setAdapter(adapter1);
        context = this;

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {

                    case DialogInterface.BUTTON_NEUTRAL:
                        Intent intent=new Intent(getBaseContext(),ViewItemsOrdered.class);
                        intent.putExtra("orderid",lstOrderID.get(position));
                      //  intent.putExtra("resid",resid);
                        startActivity(intent);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("What do you want?")
                .setNeutralButton("View Items Requested", dialogClickListener)
                .show();
    }
});

        mDatabaseReference.child(Constants.booking_details).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lstOrderID.clear();
                lstHotels.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if((dataSnapshot1.child("userid").getValue().toString().equalsIgnoreCase(FirebaseAuth.getInstance().getUid())&&dataSnapshot1.child("status").getValue().toString().equalsIgnoreCase("pending"))){
                        lstHotels.add(dataSnapshot1.child("name").getValue().toString() +
                                "\nPhone:" + dataSnapshot1.child("phone").getValue().toString() +
                                "\nAmount:" + dataSnapshot1.child("total_amount").getValue().toString());
                        lstOrderID.add(dataSnapshot1.getKey());
                        lstHistoryStatus.add(dataSnapshot1.child("status").getValue().toString());
                    }
                }
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //String menu_name, menu_price, menu_type, menu_category;


}
