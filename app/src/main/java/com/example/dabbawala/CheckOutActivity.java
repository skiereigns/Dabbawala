package com.example.dabbawala;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CheckOutActivity extends AppCompatActivity {
    EditText txtAddress;
    EditText txtPhone;
    TextView txtLocation;
    CheckBox cbxAddress;
    SharedPreferences preferences;
    String total_sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtAddress = findViewById(R.id.txtAddress);
        txtLocation = findViewById(R.id.txtLocation);
        cbxAddress = findViewById(R.id.cbxAddress);
        txtPhone = findViewById(R.id.txtPhone);

        total_sum = getIntent().getStringExtra("total_cost");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // txtLocation.setText();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        txtLocation.setText("Location: " + preferences.getString("latitude", "0.0") + "," + preferences.getString("longitude", "0.0"));
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

    }

    DatabaseReference mDatabaseReference;

    public void proceed(View v) {
        String deliver_address = txtAddress.getText().toString();

        String phone = txtPhone.getText().toString();
        if (cbxAddress.isChecked()) {
            Intent intent = new Intent(getBaseContext(), PaymentOption.class);
            intent.putExtra("delivery_address", "current_location");
            intent.putExtra("phone", phone);
            intent.putExtra("total_cost", total_sum);
            startActivity(intent);
            finish();

        } else {
            Intent intent = new Intent(getBaseContext(), PaymentOption.class);
            intent.putExtra("delivery_address", deliver_address);
            intent.putExtra("phone", phone);
            intent.putExtra("total_cost", total_sum);
            startActivity(intent);
            finish();
        }


    }
}
