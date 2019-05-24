package com.example.dabbawala;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewPastOrders extends AppCompatActivity {

    Spinner spnType, spnCategory;
    DatabaseReference mDatabaseReference;
    ArrayList<String> lstHotels = new ArrayList<>();
    ArrayList<String> lstHotelsID = new ArrayList<>();
    ArrayList<String> lstHistoryStatus = new ArrayList<>();
    ArrayList<String> lstLatitude = new ArrayList<>();
    ArrayList<String> lstLongitude = new ArrayList<>();
    ArrayList<String> lstUserToken = new ArrayList<>();
    ArrayList<String> lstUserid = new ArrayList<>();

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
        Toast.makeText(context, "Long press on item with no address", Toast.LENGTH_SHORT).show();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:


                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                mDatabaseReference.child(Constants.booking_details).
                                        child(lstHotelsID.get(position)).child("status").setValue("Cancelled");
                                break;
                            case DialogInterface.BUTTON_NEUTRAL:
                                Intent intent = new Intent(getBaseContext(), ViewItemsOrdered.class);
                                intent.putExtra("orderid", lstHotelsID.get(position));
                                intent.putExtra("resid", resid);
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
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                String uri = "http://maps.google.com/maps?daddr=" + lstLatitude.get(position) + "," + lstLongitude.get(position);
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                                intent.setComponent((new ComponentName("com.google.android.apps.maps",
                                        "com.google.android.maps.MapsActivity")));
                                startActivity(intent);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                Intent intent1 = new Intent(getBaseContext(), ChatActivity.class);
                                intent1.putExtra("userid", preferences.getString("resid", ""));
                                intent1.putExtra("fuserid", lstUserid.get(position));
                                startActivity(intent1);
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("What do you want?").setPositiveButton("View Location", dialogClickListener)
                        .setNegativeButton("Chat", dialogClickListener).show();
                return true;
            }
        });
        mDatabaseReference.child(Constants.booking_details).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lstHotelsID.clear();
                lstHotels.clear();
                lstUserid.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (!(dataSnapshot1.child("resid").getValue().toString().equalsIgnoreCase(resid) && dataSnapshot1.child("status").getValue().toString().equalsIgnoreCase("pending"))) {
                        if (dataSnapshot1.child("delivery_address").getValue().toString().equalsIgnoreCase("current_location")) {
                            lstHotels.add(dataSnapshot1.child("name").getValue().toString() +
                                    "\nPhone:" + dataSnapshot1.child("phone").getValue().toString() +
                                    "\nAmount:" + dataSnapshot1.child("total_amount").getValue().toString() +
                                    "\nStatus:" + dataSnapshot1.child("status").getValue().toString()
                            );
                            lstLatitude.add(dataSnapshot1.child("latitude").getValue().toString());
                            lstLongitude.add(dataSnapshot1.child("longitude").getValue().toString());
                            lstHotelsID.add(dataSnapshot1.getKey());
                            lstHistoryStatus.add(dataSnapshot1.child("status").getValue().toString());

                        } else {
                            lstHotels.add(dataSnapshot1.child("name").getValue().toString() +
                                    "\nPhone:" + dataSnapshot1.child("phone").getValue().toString() +
                                    "\nAmount:" + dataSnapshot1.child("total_amount").getValue().toString() +
                                    "\nDelivery Address:" + dataSnapshot1.child("delivery_address").getValue().toString() +
                                    "\nStatus:" + dataSnapshot1.child("status").getValue().toString()

                            );
                            lstHotelsID.add(dataSnapshot1.getKey());
                            lstHistoryStatus.add(dataSnapshot1.child("status").getValue().toString());
                            lstLatitude.add("0.0");
                            lstLongitude.add("0.0");


                        }
                        lstUserid.add(dataSnapshot1.child("userid").getValue().toString());
                        lstUserToken.add(dataSnapshot1.child("token_id").getValue().toString());

                    }


                }
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    class SENDFCM extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            FireBase fb = new FireBase();
            return fb.send(params[0], params[1]);

        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            try {
                JSONObject jObj = new JSONObject(aVoid);
                if (jObj.getInt("success") == 1) {
                    Toast.makeText(context, "Notification sent successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Failed to send notification", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
