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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewMenu extends AppCompatActivity {

    Spinner spnType, spnCategory;
    DatabaseReference mDatabaseReference;
    ArrayList<String> lstHotels = new ArrayList<>();
    ArrayList<String> lstHotelsID = new ArrayList<>();
    ArrayList<String> lstMenuPrice = new ArrayList<>();
    ListView listView;
    Adapter1 adapter1;
    String resid;
    Context context;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        resid = getIntent().getStringExtra("resid");
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
                                                            case DialogInterface.BUTTON_POSITIVE:
                                                                //Yes button clicked
                                                                JSONArray jsonArray;
                                                                try {
                                                                    jsonArray = new JSONArray(preferences.getString("cart_items", null));
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                    jsonArray = new JSONArray();
                                                                }
                                                                if(jsonArray.length()>0){
                                                                    if (jsonArray.toString().contains(resid)) {
                                                                        if (!jsonArray.toString().contains(lstHotelsID.get(position))) {
                                                                            Intent intent = new Intent(getBaseContext(), ListCartActivitiy.class);
                                                                            intent.putExtra("pid", lstHotelsID.get(position));
                                                                            intent.putExtra("pname", lstHotels.get(position));
                                                                            intent.putExtra("pdesc", resid);
                                                                            intent.putExtra("pprice", Integer.parseInt(lstMenuPrice.get(position)));
                                                                            intent.putExtra("pquantity", 1);
                                                                            startActivity(intent);

                                                                        } else {
                                                                            Toast.makeText(context, "Item Already in cart", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }else{
                                                                        Toast.makeText(context, "Please add item from one hotel only", Toast.LENGTH_SHORT).show();
                                                                    }

                                                                }else if(jsonArray.length()==0){
                                                                    if (!jsonArray.toString().contains(lstHotelsID.get(position))) {
                                                                        Intent intent = new Intent(getBaseContext(), ListCartActivitiy.class);
                                                                        intent.putExtra("pid", lstHotelsID.get(position));
                                                                        intent.putExtra("pname", lstHotels.get(position));
                                                                        intent.putExtra("pdesc", resid);
                                                                        intent.putExtra("pprice", Integer.parseInt(lstMenuPrice.get(position)));
                                                                        intent.putExtra("pquantity", 1);
                                                                        startActivity(intent);

                                                                    } else {
                                                                        Toast.makeText(context, "Item Already in cart", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }



                                                                break;

                                                            case DialogInterface.BUTTON_NEGATIVE:
                                                                //No button clicked
                                                                break;
                                                        }
                                                    }
                                                };

                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                builder.setMessage("Add To Cart?").setPositiveButton("Yes", dialogClickListener)
                                                        .setNegativeButton("No", dialogClickListener).show();
                                            }
                                        }
        );
        mDatabaseReference.child(Constants.resturant).child(resid).child(Constants.menu).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lstHotelsID.clear();
                lstHotels.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    lstHotels.add(dataSnapshot1.child("menu_name").getValue().toString() +
                            "\nPrice:" + dataSnapshot1.child("menu_price").getValue().toString() +
                            "\nType:" + dataSnapshot1.child("menu_type").getValue().toString() +
                            "\nCategory:" + dataSnapshot1.child("menu_category").getValue().toString()
                    );
                    lstHotelsID.add(dataSnapshot1.getKey());
                    lstMenuPrice.add(dataSnapshot1.child("menu_price").getValue().toString());

                }
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




}
