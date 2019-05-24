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
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddItem extends AppCompatActivity {
EditText txtName,txtPrice;
Spinner spnType,spnCategory;
DatabaseReference mDatabaseReference;
    ArrayList<String> lstHotels = new ArrayList<>();
    ArrayList<String> lstHotelsID = new ArrayList<>();
    ListView listView;
    Adapter1 adapter1;
String resid;
Context context;
SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtName=findViewById(R.id.txtName);
        txtPrice=findViewById(R.id.txtPrice);
        spnType=findViewById(R.id.spnType);
        spnCategory=findViewById(R.id.spnCategory);
        resid=getIntent().getStringExtra("resid");
        listView=findViewById(R.id.list);
        adapter1=new Adapter1(this,R.layout.custum_list,lstHotels);
        listView.setAdapter(adapter1);
        context=this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
mDatabaseReference= FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        switch (which) {
                                                            case DialogInterface.BUTTON_POSITIVE:
                                                                //Yes button clicked
                                                                mDatabaseReference.child(Constants.resturant).child(resid).child(Constants.menu).child(lstHotelsID.get(position)).removeValue();

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
        mDatabaseReference.child(Constants.resturant).child(resid).child(Constants.menu).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lstHotelsID.clear();
                lstHotels.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    lstHotels.add(dataSnapshot1.child("menu_name").getValue().toString()+
                            "\nPrice:"+dataSnapshot1.child("menu_price").getValue().toString()+
                            "\nType:"+dataSnapshot1.child("menu_type").getValue().toString()+
                            "\nCategory:"+dataSnapshot1.child("menu_category").getValue().toString()
                    );
                    lstHotelsID.add(dataSnapshot1.getKey());

                }
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    String menu_name, menu_price, menu_type, menu_category;

    public void saveRes(View v) {
        menu_name = txtName.getText().toString();
        menu_price = txtPrice.getText().toString();
        menu_type = spnType.getSelectedItem().toString();
        menu_category = spnCategory.getSelectedItem().toString();

        Map map = new HashMap();
        map.put("menu_name", menu_name);
        map.put("menu_price", menu_price);
        map.put("menu_type", menu_type);
        map.put("menu_category", menu_category);
        if (menu_name.length() > 0) {
            if (menu_price.length() > 0) {

                    mDatabaseReference.child(Constants.resturant).child(resid).child(Constants.menu).push().setValue(map);
                    Toast.makeText(this, "Menu created successfully", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Please check price", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please check menu name", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.res_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_current_order) {
            Intent intent=new Intent(getBaseContext(),ViewCurrentOrders.class);
            intent.putExtra("resid",resid);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_past_order) {
            Intent intent=new Intent(getBaseContext(),ViewPastOrders.class);
            intent.putExtra("resid",resid);
            startActivity(intent);
        } if (id == R.id.action_logout) {
         preferences.edit().remove("resid").commit();
            Intent intent=new Intent(getBaseContext(),ResLogin.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
