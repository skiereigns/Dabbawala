package com.example.dabbawala;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewItemsOrdered extends AppCompatActivity {

    Spinner spnType, spnCategory;
    DatabaseReference mDatabaseReference;
    ArrayList<String> lstHotels = new ArrayList<>();
    ArrayList<String> lstHotelsID = new ArrayList<>();
    ArrayList<String> lstHistoryStatus = new ArrayList<>();
    ListView listView;
    Adapter1 adapter1;
   // String resid;
    Context context;
    SharedPreferences preferences;
String orderid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        orderid = getIntent().getStringExtra("orderid");
        //resid = getIntent().getStringExtra("resid");
        listView = findViewById(R.id.list);
        adapter1 = new Adapter1(this, R.layout.custum_list, lstHotels);
        listView.setAdapter(adapter1);
        context = this;

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);



        mDatabaseReference.child(Constants.booking_details).child(orderid)
                .child(Constants.items).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lstHotelsID.clear();
                lstHotels.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    lstHotels.add(dataSnapshot1.child("productname").getValue().toString() +
                            "\nQuantity:" + dataSnapshot1.child("quantity").getValue().toString() +
                            "\nCost:" + dataSnapshot1.child("cost").getValue().toString()

                    );

                }
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

   // String menu_name, menu_price, menu_type, menu_category;


}
