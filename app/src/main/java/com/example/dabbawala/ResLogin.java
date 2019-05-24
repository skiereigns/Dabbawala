package com.example.dabbawala;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ResLogin extends AppCompatActivity {
    DatabaseReference mDatabaseReference;
    EditText txtUname, txtpass;
SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtUname = findViewById(R.id.txtUname);
        txtpass = findViewById(R.id.txtpass);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
mDatabaseReference= FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);
    if(preferences.getString("resid","").length()>0){
        Intent intent = new Intent(getBaseContext(), AddItem.class);
        intent.putExtra("resid", preferences.getString("resid",""));
        startActivity(intent);
finish();
    }else {

    }
    }

    String uname, respass;

    public void login(View v) {
        uname = txtUname.getText().toString();
        respass = txtpass.getText().toString();
        if (uname.length() > 0) {
            if (respass.length() > 0) {

                mDatabaseReference.child(Constants.resturant).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (uname.equals(dataSnapshot1.child("resid").getValue().toString())
                                    && respass.equals(dataSnapshot1.child("respass").getValue().toString())) {
                                Intent intent = new Intent(getBaseContext(), AddItem.class);
                                intent.putExtra("6" +
                                        "", dataSnapshot1.getKey());
                                startActivity(intent);
                                preferences.edit().putString("resid",dataSnapshot1.getKey()).commit();
                                uname="";
                                respass="";
                                finish();

                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            } else {
                Toast.makeText(this, "Please check password", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please check username", Toast.LENGTH_SHORT).show();
        }

    }
}
